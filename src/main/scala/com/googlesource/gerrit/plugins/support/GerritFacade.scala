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

import com.google.gerrit.extensions.restapi.TopLevelResource
import com.google.gerrit.server.account.AccountLimits
import com.google.gerrit.server.plugins.{ListPlugins, PluginsCollection}
import com.google.gson.JsonElement
import com.google.inject.{ImplementedBy, Inject}
import com.googlesource.gerrit.plugins.support.latest.LatestCapabilityControl

import scala.util.{Failure, Success, Try}

object GerritFacade {

  class PluginName(val value: String) extends AnyRef

  implicit class PimpedCapabilityControl(val cc: AccountLimits) extends AnyVal {

    def canDo(operation: String)(implicit pluginName: PluginName) =
      LatestCapabilityControl(cc).canPerform(operation)
        .get
  }
}

object TryAll {
  def apply[T](block: => T): Try[T] = {
    try {
      val res:T = block
      Success(res)
    } catch {
      case t: Throwable => Failure(t)
    }
  }
}

// Structure of the JSON fields returned by the pluginsInfo method
case class PluginInfo(id: String, version: String, indexUrl: String, disabled: Boolean)

@ImplementedBy(classOf[GerritPluginsInfoProvider])
trait PluginsInfoProvider {

  def getPluginsInfo: JsonElement
}

class GerritPluginsInfoProvider @Inject() (val pluginsCollection: PluginsCollection) extends PluginsInfoProvider {

  override def getPluginsInfo: JsonElement = {
    pluginsCollection
      .list
      .asInstanceOf[ListPlugins]
      .apply(TopLevelResource.INSTANCE).asInstanceOf[JsonElement]
  }
}
