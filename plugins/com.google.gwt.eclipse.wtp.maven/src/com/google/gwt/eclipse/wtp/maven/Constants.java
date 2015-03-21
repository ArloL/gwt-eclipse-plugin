/*******************************************************************************
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.google.gwt.eclipse.wtp.maven;

import com.google.gwt.eclipse.wtp.facet.data.IGwtFacetConstants;


public class Constants {

  /**
   * Components of gwt-maven-plugin Maven coordinates:
   */
  public static final String GWT_GROUP_ID = "com.google.gwt";

  /**
   * org.codehaus.mojo:gwt-maven-plugin
   *
   * TODO consider Thomas maven plugin too
   */
  public static final String GWT_MAVEN_PLUGIN_ARTIFACT_ID = "gwt-maven-plugin";

  /**
   * Facet ID
   */
  public static final String GAE_WAR_FACET_ID = IGwtFacetConstants.GWT_FACET_ID;

}
