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
package com.google.appengine.eclipse.wtp.wizards;

import com.google.appengine.eclipse.wtp.AppEnginePlugin;
import com.google.appengine.eclipse.wtp.runtime.GaeRuntime;
import com.google.appengine.eclipse.wtp.runtime.RuntimeUtils;
import com.google.appengine.eclipse.wtp.server.GaeServer;
import com.google.common.collect.Maps;
import com.google.gdt.eclipse.core.StatusUtilities;
import com.google.gdt.eclipse.core.sdk.SdkUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import java.util.Map;

public final class GaeServerWizardFragment extends WizardFragment {

  private IWizardHandle wizard;
  private Text serverPortText;
  private Text autoreloadText;
  private Label autoreloadLabel;
  private Text hrdUnappliedPctText;

  private String serverPort;
  private String autoreloadTime;
  private String hrdUnappliedJobPercentage;

  @Override
  public Composite createComposite(Composite parent, IWizardHandle handle) {
    wizard = handle;
    // do create UI
    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new GridLayout(2, false));
    handle.setImageDescriptor(ImageDescriptor.createFromURL(AppEnginePlugin.getInstance().getBundle().getEntry(
        "/icons/ae-deploy_90x79.png")));
    handle.setTitle("Google App Engine Development Server");
    handle.setDescription("Enter the configuration parameters for Google App Engine");
    {
      Label serverportLabel = new Label(container, SWT.NONE);
      serverportLabel.setText("Server Port");
      serverPortText = new Text(container, SWT.SHADOW_IN | SWT.BORDER);
      serverPortText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
      serverPortText.setText(GaeServer.DEFAULT_SERVER_PORT);
    }
    // do not show auto-reload delay controls as not applicable for SDKs < 1.8.1
    GaeRuntime gaeRuntime = GaeServer.getGaeServer(getServerWorkingCopy()).getGaeRuntime();
    boolean isUsingAutoreload = true;
    if (gaeRuntime != null) {
      isUsingAutoreload = SdkUtils.compareVersionStrings(gaeRuntime.getGaeSdkVersion(),
          RuntimeUtils.MIN_SDK_VERSION_USING_AUTORELOAD) >= 0;
    }
    if (isUsingAutoreload) {
      autoreloadLabel = new Label(container, SWT.NONE);
      autoreloadLabel.setText("Auto scan for resources change, sec (0 - never)");
      autoreloadText = new Text(container, SWT.SHADOW_IN | SWT.BORDER);
      autoreloadText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
      autoreloadText.setText(GaeServer.DEFAULT_AUTORELOAD_TIME);
    }
    {
      Label hrdUnappliedPctLabel = new Label(container, SWT.NONE);
      hrdUnappliedPctLabel.setText("HRD: unapplied job percentage (0 - disable HRD)");
      hrdUnappliedPctText = new Text(container, SWT.SHADOW_IN | SWT.BORDER);
      hrdUnappliedPctText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
      hrdUnappliedPctText.setText(GaeServer.DEFAULT_HRD_UNAPPLIED_JOB_PCT);
    }
    // add listeners
    addListeners();
    Dialog.applyDialogFont(parent);
    return container;
  }

  @Override
  public boolean hasComposite() {
    return true;
  }

  @Override
  public void performCancel(IProgressMonitor monitor) throws CoreException {
    resetInputValues();
  }

  @Override
  public void performFinish(IProgressMonitor monitor) throws CoreException {
    // getting there means that the server properties are valid: either user didn't modify default
    // settings (even didn't go to this page) or passed thru validation earlier.
    GaeServer gaeServer = getGaeServer();
    gaeServer.setServerInstanceProperties(getServerProperties());
    resetInputValues();
  }

  private void addListeners() {
    ModifyListener defaultListener = new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        updateFields();
        setComplete(validate());
        wizard.update();
      }
    };
    serverPortText.addModifyListener(defaultListener);
    autoreloadText.addModifyListener(defaultListener);
    hrdUnappliedPctText.addModifyListener(defaultListener);
  }

  /**
   * @return {@link GaeServer} instance using server working copy. Throws an exception if no server
   *         delegate installed.
   */
  private GaeServer getGaeServer() throws CoreException {
    GaeServer gaeServer = GaeServer.getGaeServer(getServerWorkingCopy());
    if (gaeServer == null) {
      throw new CoreException(StatusUtilities.newErrorStatus("Cannot get GaeServer working copy.",
          AppEnginePlugin.PLUGIN_ID));
    }
    return gaeServer;
  }

  /**
   * Returns the server instance property name/value pairs.
   */
  private Map<String, String> getServerProperties() {
    Map<String, String> propertyMap = Maps.newHashMap();
    // null values are OK, GaeServer returns default values if the property is not set.
    propertyMap.put(GaeServer.PROPERTY_SERVERPORT, serverPort);
    propertyMap.put(GaeServer.PROPERTY_AUTORELOAD_TIME, autoreloadTime);
    propertyMap.put(GaeServer.PROPERTY_HRD_UNAPPLIED_JOB_PCT, hrdUnappliedJobPercentage);
    return propertyMap;
  }

  /**
   * @return the {@link IServerWorkingCopy} for this Server instance creation.
   */
  private IServerWorkingCopy getServerWorkingCopy() {
    return (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
  }

  /**
   * Do cleanup values as this fragment instance could be reused.
   */
  private void resetInputValues() {
    serverPort = null;
    autoreloadTime = null;
    hrdUnappliedJobPercentage = null;
  }

  /**
   * Updates fields by user input.
   */
  private void updateFields() {
    serverPort = serverPortText.getText().trim();
    autoreloadTime = autoreloadText.getText().trim();
    hrdUnappliedJobPercentage = hrdUnappliedPctText.getText().trim();
  }

  /**
   * @return <code>true</code> if user input is valid, <code>false</code> otherwise.
   */
  private boolean validate() {
    IStatus status = null;
    try {
      GaeServer gaeServer = getGaeServer();
      gaeServer.setServerInstanceProperties(getServerProperties());
      status = gaeServer.validate();
    } catch (CoreException e) {
      status = e.getStatus();
    }

    if (status == null || status.isOK()) {
      wizard.setMessage(null, IMessageProvider.NONE);
    } else {
      wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
      return false;
    }
    return true;
  }
}
