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

package com.googlesource.gerrit.plugins.support.commands

import java.io.File
import java.nio.file.Files

import com.google.inject.Inject
import com.googlesource.gerrit.plugins.support.GerritSupportCommand.{BinResult, CommandResult}
import com.googlesource.gerrit.plugins.support._

import scala.util.{Failure, Success, Try}

class ConfigInfoCommand @Inject()(val sitePathsWrapper: SitePathsWrapper)
  extends GerritSupportCommand {

  override def getResults = {
    try {
      sitePathsWrapper
        .getAsPath("etc_dir")
        .toFile
        .listFiles
        .filter(_.isFile)
        .filter(_.getName.endsWith(".config"))
        .map(f => CommandResult(
          f.getName,
          BinResult(Files.readAllBytes(f.toPath))))
    } catch {
      case e: Exception =>
        val error =
          s"Error while processing config file listing: ${e.getMessage}"
        log.error(error)
        Seq(CommandResult(name, error))
    }
  }
}
