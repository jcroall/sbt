/*
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
package org.jutils.jhardware.model;

import java.util.List;

/** @author Javier Garcia Alonso */
public class DisplayInfo implements ComponentInfo {
  List<Display> displayDevices;

  public List<Display> getDisplayDevices() {
    return displayDevices;
  }

  public void setDisplayDevices(List<Display> displayDevices) {
    this.displayDevices = displayDevices;
  }
}
