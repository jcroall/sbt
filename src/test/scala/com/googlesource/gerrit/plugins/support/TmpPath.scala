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

import java.io.{File, PrintWriter}

trait TmpPath {
  val tmpPath = File.createTempFile(classOf[GerritSupportTest].getName, "").getParentFile

  def millis = System.currentTimeMillis.toString

  def tmpFile = {
    val file = File.createTempFile("file.txt", millis, tmpPath)
    file.deleteOnExit
    file
  }

  def tmpConfigFile = {
    val file = File.createTempFile(s"file.$millis", ".config", tmpPath)
    file.deleteOnExit()
    new PrintWriter(file) {
      try {
        write("something")
      } finally {
        close
      }
    }
    file
  }
}
