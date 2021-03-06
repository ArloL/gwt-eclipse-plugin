/*******************************************************************************
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.google.gdt.eclipse.drive.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.gdt.eclipse.drive.DrivePlugin;
import com.google.gdt.eclipse.drive.images.ImageKeys;
import com.google.gdt.eclipse.drive.model.FolderTree.FolderTreeLeaf;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider whose model is a {@link FolderTree}.
 */
public class FolderTreeLabelProvider implements ILabelProvider {
  
  private final Image folderImage;
  private final Image leafImage;
  
  public FolderTreeLabelProvider(Image leafImage) {
    // Retrieval of the folder image is done in this constructor instead of in a static declaration
    // so that it can be circumvented in unit tests by using a different constructor. Retrieval or
    // creation of an Image object requires a real workbench running on a real OS.
    this.folderImage = DrivePlugin.getDefault().getImage(ImageKeys.FOLDER_ICON);
    this.leafImage = leafImage;
  }
  
  @VisibleForTesting
  public FolderTreeLabelProvider() {
    this.folderImage = null;
    this.leafImage = null;
  }

  @Override
  public Image getImage(Object folderTree) {
    if (folderTree instanceof FolderTreeLeaf) {
      return leafImage;
    } else {
      return folderImage;
    }
  }

  @Override
  public String getText(Object folderTree) {
    return ((FolderTree) folderTree).getTitle();
  }

  @Override
  public boolean isLabelProperty(Object element, String property) {
    return false;
  }

  @Override
  public void addListener(ILabelProviderListener listener) {
  }

  @Override
  public void removeListener(ILabelProviderListener listener) {
  }

  @Override
  public void dispose() {
  }
}
