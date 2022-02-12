/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlesource.gerrit.plugins.support

import java.nio.charset.Charset

import com.google.gson.{Gson, JsonElement, JsonObject}
import com.google.inject._
import com.googlesource.gerrit.plugins.support.GerritSupportCommand.{CommandResult, JsonResult, ResultName}
import org.slf4j.LoggerFactory

import scala.util.Try

object GerritSupportCommand {

  class ResultName(val value: String) extends AnyVal

  implicit def wrapToResultName(name: String): ResultName = new ResultName(name)

  implicit def unwrapResultName(resultName: ResultName): String = resultName.value

  // allows returning a pure Json result or a textual file content
  case class CommandResult(entryName: ResultName, content: AnyResult)

  trait AnyResult {
    def getBytes: Array[Byte]
  }

  case class JsonResult(result: JsonElement)(implicit val gson: Gson) extends AnyResult {
    override def getBytes: Array[Byte] = gson.toJson(result)
  }

  case class TextResult(result: String) extends AnyResult {
    override def getBytes: Array[Byte] = result
  }

  case class BinResult(result: Array[Byte]) extends AnyResult {
    override def getBytes: Array[Byte] = result
  }

  implicit def convertAny2CommandResult(x: Any)(implicit resultName: ResultName, gson: Gson): CommandResult =
    x match {
      case res: AnyResult => CommandResult(resultName, res)
      case anyRes => CommandResult(resultName, JsonResult(gson.toJsonTree(anyRes)))
    }

  val UTF8 = Charset.forName("UTF-8")

  implicit def convertStringToBinary(x:String):Array[Byte] = x.getBytes(UTF8)
  implicit def convertString2TextResult(x:String): TextResult = TextResult(x)
  implicit def convertBinary2BinResult(x:Array[Byte]): BinResult = BinResult(x)
}

abstract class GerritSupportCommand {
  import GerritSupportCommand._

  val log = LoggerFactory.getLogger(classOf[GerritSupportCommand])

  implicit val gson = new Gson
  implicit val name: ResultName = camelToUnderscores(this.getClass.getSimpleName.stripSuffix("Command"))
    .stripPrefix("_")

  val nameJson = s"$name.json"

  def getResults: Seq[CommandResult] = Seq(getResult)

  def getResult: CommandResult = null

  def execute: Seq[CommandResult] = {
    Try {
      getResults
    } getOrElse {
      val error = s"${name} not available on ${System.getProperty("os.name")}"
      log.error(error)
      Seq(ErrorInfo("error" -> error))
    }
  }

  private def camelToUnderscores(name: String) = "[A-Z\\d]".r.replaceAllIn(name, { m =>
    "_" + m.group(0).toLowerCase()
  })
}

@Singleton
class GerritSupportCommandFactory @Inject()(val injector: Injector) {

  def apply(name: String): GerritSupportCommand =
    injector.getInstance(
      Class.forName(s"com.googlesource.gerrit.plugins.support.commands.${name.capitalize}Command")
        .asInstanceOf[Class[_ <: GerritSupportCommand]])

}

object ErrorInfo {
  def apply[T](attributes: (String, T)*)(implicit gson: Gson, resultName: ResultName): CommandResult =
    CommandResult(resultName,
      JsonResult(
        attributes.foldLeft(new JsonObject) {
          (json, pair) => {
            json.add(pair._1, gson.toJsonTree(pair._2))
            json
          }
        }))
}