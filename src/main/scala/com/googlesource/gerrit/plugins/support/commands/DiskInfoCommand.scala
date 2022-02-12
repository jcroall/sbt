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

import java.nio.file.Files

import com.google.inject.Inject
import com.googlesource.gerrit.plugins.support.{SitePathsWrapper, GerritSupportCommand}

class DiskInfoCommand @Inject()(val sitePathsFolder: SitePathsWrapper) extends GerritSupportCommand {

  case class DiskInfo(path: String, diskFree: Long, diskUsable: Long, diskTotal: Long)

  override def getResult = {
    val dataPath = sitePathsFolder.getAsPath("data_dir")
    val store = Files.getFileStore(dataPath)
    DiskInfo(dataPath.toString, store.getUnallocatedSpace,
      store.getUsableSpace, store.getTotalSpace
    )
  }
}