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

import java.io.StringWriter

import com.google.gson.JsonArray
import com.google.inject.Inject
import com.googlesource.gerrit.plugins.support.GerritSupportCommand.{CommandResult, JsonResult}
import com.googlesource.gerrit.plugins.support.{GerritSupportCommand, PluginsInfoProvider, SitePathsWrapper}

class PluginsInfoCommand @Inject()(val sitePathsWrapper: SitePathsWrapper,
                                   val pluginsInfoProvider: PluginsInfoProvider)
  extends GerritSupportCommand {

  override def getResults = {
    try {
      Seq(
        listDir("plugins_dir"),
        listDir("lib_dir"),
        listPluginsVersions
      )
    } catch {
      case e: Exception => {
        val error = s"Error while processing pluginsInfo ${e
          .getMessage}"
        log.error(error,e)
        Seq(CommandResult(name, error))
      }
    }
  }

  private def listDir(dir: String): CommandResult = {
    val jsonArray = new JsonArray()
    sitePathsWrapper
      .getAsPath(dir)
      .toFile
      .listFiles
      .map(FileAttribute.from)
      .map(gson.toJsonTree)
      .foreach(jsonArray.add)

    CommandResult(dir, JsonResult(jsonArray))
  }

  private def listPluginsVersions = {
    val outString = new StringWriter

    CommandResult("plugins_versions",
      JsonResult(gson.toJsonTree(pluginsInfoProvider.getPluginsInfo))
    )
  }
}
