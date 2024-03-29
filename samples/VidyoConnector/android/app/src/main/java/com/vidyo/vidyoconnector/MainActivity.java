/**
{file:
	{name: MainActivity.java}
	{description: .}
	{copyright:
		(c) 2016-2018 Vidyo, Inc.,
		433 Hackensack Avenue, 7th Floor,
		Hackensack, NJ  07601.

		All rights reserved.

		The information contained herein is proprietary to Vidyo, Inc.
		and shall not be reproduced, copied (in whole or in part), adapted,
		modified, disseminated, transmitted, transcribed, stored in a retrieval
		system, or translated into any language in any form by any means
		without the express written consent of Vidyo, Inc.
		                  ***** CONFIDENTIAL *****
	}
}
*/
package com.vidyo.vidyoconnector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Device.Device;
import com.vidyo.VidyoClient.Device.LocalCamera;
import com.vidyo.VidyoClient.Endpoint.ChatMessage;
import com.vidyo.VidyoClient.Endpoint.LogRecord;
import com.vidyo.VidyoClient.Endpoint.Participant;
import com.vidyo.VidyoClient.NetworkInterface;

public class MainActivity extends FragmentActivity implements
        View.OnClickListener,
        Connector.IConnect,
        Connector.IRegisterLogEventListener,
        Connector.IRegisterNetworkInterfaceEventListener,
        Connector.IRegisterLocalCameraEventListener, Connector.IRegisterMessageEventListener,
        IVideoFrameListener {

    // Define the various states of this application.
    enum VidyoConnectorState {
        Connecting,
        Connected,
        Disconnecting,
        Disconnected,
        DisconnectedUnexpected,
        Failure,
        FailureInvalidResource
    }

    // Map the application state to the status to display in the toolbar.
    private static final Map<VidyoConnectorState, String> mStateDescription = new HashMap<VidyoConnectorState, String>() {{
        put(VidyoConnectorState.Connecting, "Connecting...");
        put(VidyoConnectorState.Connected, "Connected");
        put(VidyoConnectorState.Disconnecting, "Disconnecting...");
        put(VidyoConnectorState.Disconnected, "Disconnected");
        put(VidyoConnectorState.DisconnectedUnexpected, "Unexpected disconnection");
        put(VidyoConnectorState.Failure, "Connection failed");
        put(VidyoConnectorState.FailureInvalidResource, "Invalid Resource ID");
    }};

    // Helps check whether app has permission to access what is declared in its manifest.
    // - Permissions from app's manifest that have a "protection level" of "dangerous".
    private static final String[] mPermissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    // - This arbitrary, app-internal constant represents a group of requested permissions.
    // - For simplicity, this app treats all desired permissions as part of a single group.
    private final int PERMISSIONS_REQUEST_ALL = 1988;

    private VidyoConnectorState mVidyoConnectorState = VidyoConnectorState.Disconnected;
    private boolean mVidyoClientInitialized = false;
    private Logger mLogger = Logger.getInstance();
//    private Connector mVidyoConnector = null;
    private LocalCamera mLastSelectedCamera = null;
    private ToggleButton mToggleConnectButton;
    private ToggleButton mMicrophonePrivacyButton;
    private ToggleButton mCameraPrivacyButton;
    private ProgressBar mConnectionSpinner;
    private LinearLayout mControlsLayout;
    private LinearLayout mToolbarLayout;
    private EditText mHost;
    private EditText mDisplayName;
    private EditText mToken;
    private EditText mResourceId;
    private TextView mToolbarStatus;
    private TextView mClientVersion;
    private VideoFrameLayout mVideoFrame;
    private boolean mHideConfig = false;
    private boolean mAutoJoin = false;
    private boolean mAllowReconnect = true;
    private boolean mCameraPrivacy = false;
    private boolean mMicrophonePrivacy = false;
    private boolean mEnableDebug = false;
    private String mReturnURL = null;
    private String mExperimentalOptions = null;
    private boolean mRefreshSettings = true;
    private boolean mDevicesSelected = true;
    private boolean mVidyoCloudJoin = false;
    private String mPortal;  // Used for VidyoCloud systems, not Vidyo.io
    private String mRoomKey; // Used for VidyoCloud systems, not Vidyo.io
    private String mRoomPin; // Used for VidyoCloud systems, not Vidyo.io
    private FragmentTabHost mTabHost;
    /*
     *  Operating System Events
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.Log("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("Call").setIndicator("Call"),
                CallFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Chat").setIndicator("Chat"),
                ChatFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Participant").setIndicator("Participant"),
                ParticipantFragment.class, null);
//        mTabHost.setCurrentTab(1);
//        mTabHost.setCurrentTab(0);
        mTabHost.setup(this,this.getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mLogger.Log("onNewIntent");
        super.onNewIntent(intent);

        // Set the refreshSettings flag so the app settings are refreshed in onStart
        mRefreshSettings = true;

        // New intent was received so set it to use in onStart
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        mLogger.Log("onStart");
        super.onStart();

        // Initialize or refresh the app settings.
        // When app is first launched, mRefreshSettings will always be true.
        // Each successive time that onStart is called, app is coming back to foreground so check if the
        // settings need to be refreshed again, as app may have been launched via URI.
//        if (mRefreshSettings &&
//            mVidyoConnectorState != VidyoConnectorState.Connected &&
//            mVidyoConnectorState != VidyoConnectorState.Connecting) {
//
//            Intent intent = getIntent();
//            Uri uri = intent.getData();
//
//            // Check if app was launched via URI
//            if (uri != null) {
//                String param = uri.getQueryParameter("host");
//                mHost.setText( param != null ? param : "prod.vidyo.io");
//
//                param = uri.getQueryParameter("token");
//                mToken.setText(param != null ? param : "");
//
//                param = uri.getQueryParameter("displayName");
//                mDisplayName.setText(param != null ? param : "Demo User");
//
//                param = uri.getQueryParameter("resourceId");
//                mResourceId.setText(param != null ? param : "");
//
//                mReturnURL = uri.getQueryParameter("returnURL");
//                mHideConfig = uri.getBooleanQueryParameter("hideConfig", false);
//                mAutoJoin = uri.getBooleanQueryParameter("autoJoin", false);
//                mAllowReconnect = uri.getBooleanQueryParameter("allowReconnect", true);
//                mCameraPrivacy = uri.getBooleanQueryParameter("cameraPrivacy", false);
//                mMicrophonePrivacy = uri.getBooleanQueryParameter("microphonePrivacy", false);
//                mEnableDebug = uri.getBooleanQueryParameter("enableDebug", false);
//                mExperimentalOptions = uri.getQueryParameter("experimentalOptions");
//
//                ///////////////////////////////////////////////////////////////////////////////////////
//                // Note: the following parameters are used to connect to VidyoCloud systems, not Vidyo.io.
//                mVidyoCloudJoin = (uri.getHost() != null) && uri.getHost().equalsIgnoreCase("join");
//                if (mVidyoCloudJoin) {
//                    // Do not display the Vidyo.io form in VidyoCloud mode.
//                    mHideConfig = true;
//
//                    // Populate portal, roomKey, and roomPin
//                    param = uri.getQueryParameter("portal");
//                    mPortal = param != null ? param : "";
//                    param = uri.getQueryParameter("roomKey");
//                    mRoomKey = param != null ? param : "";
//                    param = uri.getQueryParameter("roomPin");
//                    mRoomPin = param != null ? param : "";
//                }
//                ///////////////////////////////////////////////////////////////////////////////////////
//            } else {
//                // If this app was launched by a different app, then get any parameters; otherwise use default settings.
//                mHost.setText(intent.hasExtra("host") ? intent.getStringExtra("host") : "prod.vidyo.io");
//                mToken.setText(intent.hasExtra("token") ? intent.getStringExtra("token") : "cHJvdmlzaW9uAFN0ZXZlQDM3ZjBiZC52aWR5by5pbwA2MzcxODA0MDU5MQAAMzNlZjc3NThkMDZlZjNlYTNjZjVmYzk2OWNlN2RjODkyOGUzNzdhZGVhOWZhOGI3YmY4MDRjNmJjOTA5ZDllMTViZDFjZWFkOWZiYzAzMTljN2EwZDkxM2I3YmNhMmIw");
//                mDisplayName.setText(intent.hasExtra("displayName") ? intent.getStringExtra("displayName") : "DemoUser");
//                mResourceId.setText(intent.hasExtra("resourceId") ? intent.getStringExtra("resourceId") : "DemoRoom");
//                mReturnURL = intent.hasExtra("returnURL") ? intent.getStringExtra("returnURL") : null;
//                mHideConfig = intent.getBooleanExtra("hideConfig", false);
//                mAutoJoin = intent.getBooleanExtra("autoJoin", false);
//                mAllowReconnect = intent.getBooleanExtra("allowReconnect", true);
//                mCameraPrivacy = intent.getBooleanExtra("cameraPrivacy", false);
//                mMicrophonePrivacy = intent.getBooleanExtra("microphonePrivacy", false);
//                mEnableDebug = intent.getBooleanExtra("enableDebug", false);
//                mExperimentalOptions = intent.hasExtra("experimentalOptions") ? intent.getStringExtra("experimentalOptions") : null;
//                mVidyoCloudJoin = false;
//            }
//
//            mLogger.Log("onStart: hideConfig = " + mHideConfig + ", autoJoin = " + mAutoJoin + ", allowReconnect = " + mAllowReconnect + ", enableDebug = " + mEnableDebug);
//
//            // Hide the form if hideConfig enabled.
//            if (mHideConfig) {
//                mControlsLayout.setVisibility(View.GONE);
//            }
//
//            // Apply the app settings.
//            this.applySettings();
//        }
//        mRefreshSettings = false;
    }

    @Override
    protected void onResume() {
        mLogger.Log("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLogger.Log("onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLogger.Log("onStop");
        super.onStop();

//        if (mVidyoConnector != null) {
//            if (mVidyoConnectorState != VidyoConnectorState.Connected &&
//                mVidyoConnectorState != VidyoConnectorState.Connecting) {
//                // Not connected/connecting to a resource.
//                // Release camera, mic, and speaker from this app while backgrounded.
//                mVidyoConnector.selectLocalCamera(null);
//                mVidyoConnector.selectLocalMicrophone(null);
//                mVidyoConnector.selectLocalSpeaker(null);
//                mDevicesSelected = false;
//            }
//            mVidyoConnector.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Background);
//        }
    }

    @Override
    protected void onRestart() {
        mLogger.Log("onRestart");
        super.onRestart();

//        if (mVidyoConnector != null) {
//            mVidyoConnector.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Foreground);
//
//            if (!mDevicesSelected) {
//                // Devices have been released when backgrounding (in onStop). Re-select them.
//                mDevicesSelected = true;
//
//                // Select the previously selected local camera and default mic/speaker
//                mVidyoConnector.selectLocalCamera(mLastSelectedCamera);
//                mVidyoConnector.selectDefaultMicrophone();
//                mVidyoConnector.selectDefaultSpeaker();
//
//                // Reestablish camera and microphone privacy states
//                mVidyoConnector.setCameraPrivacy(mCameraPrivacy);
//                mVidyoConnector.setMicrophonePrivacy(mMicrophonePrivacy);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        mLogger.Log("onDestroy");
        super.onDestroy();

        // Release device resources
//        mLastSelectedCamera = null;
//        if (mVidyoConnector != null) {
//            mVidyoConnector.disable();
//        }
//
//        // Connector will be destructed upon garbage collection.
//        mVidyoConnector = null;

        ConnectorPkg.setApplicationUIContext(null);

        // Uninitialize the VidyoClient library - this should be done once in the lifetime of the application.
        ConnectorPkg.uninitialize();
    }

    // The device interface orientation has changed
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.Log("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    /*
     * Private Utility Functions
     */

    // Callback containing the result of the permissions request. If permissions were not previously obtained,
    // wait until this is received until calling startVideoViewSizeListener where Connector is initially rendered.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        mLogger.Log("onRequestPermissionsResult: number of requested permissions = " + permissions.length);
//
//        // If the expected request code is received, begin rendering video.
//        if (requestCode == PERMISSIONS_REQUEST_ALL) {
//            for (int i = 0; i < permissions.length; ++i)
//                mLogger.Log("permission: " + permissions[i] + " " + grantResults[i]);
//
//            // Begin listening for video view size changes.
//            this.startVideoViewSizeListener();
//        } else {
//            mLogger.Log("ERROR! Unexpected permission requested. Video will not be rendered.");
//        }
    }

    // Listen for UI changes to the view where the video is rendered.
    private void startVideoViewSizeListener() {
        mLogger.Log("startVideoViewSizeListener");

        // Render the video each time that the video view (mVideoFrame) is resized. This will
        // occur upon activity creation, orientation changes, and when foregrounding the app.
        ViewTreeObserver viewTreeObserver = mVideoFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Specify the width/height of the view to render to.
                    mLogger.Log("showViewAt: width = " + mVideoFrame.getWidth() + ", height = " + mVideoFrame.getHeight());
//                    mVidyoConnector.showViewAt(mVideoFrame, 0, 0, mVideoFrame.getWidth(), mVideoFrame.getHeight());
                }
            });
        } else {
            mLogger.Log("ERROR in startVideoViewSizeListener! Video will not be rendered.");
        }
    }

    // Apply some of the app settings
    private void applySettings() {
//        if (mVidyoConnector != null) {
//            // If enableDebug is configured then enable debugging
//            if (mEnableDebug) {
//                mVidyoConnector.enableDebug(7776, "warning info@VidyoClient info@VidyoConnector");
//                mClientVersion.setVisibility(View.VISIBLE);
//            } else {
//                mVidyoConnector.disableDebug();
//            }
//
//            // If cameraPrivacy is configured then mute the camera
//            mCameraPrivacyButton.setChecked(false); // reset state
//            if (mCameraPrivacy) {
//                mCameraPrivacyButton.performClick();
//            }
//
//            // If microphonePrivacy is configured then mute the microphone
//            mMicrophonePrivacyButton.setChecked(false); // reset state
//            if (mMicrophonePrivacy) {
//                mMicrophonePrivacyButton.performClick();
//            }
//
//            // Set experimental options if any exist
//            if (mExperimentalOptions != null) {
//                ConnectorPkg.setExperimentalOptions(mExperimentalOptions);
//            }
//
//            // If configured to auto-join, then simulate a click of the toggle connect button
//            if (mAutoJoin) {
//                mToggleConnectButton.performClick();
//            }
//        }
    }

    // The state of the VidyoConnector connection changed, reconfigure the UI.
    // If connected, dismiss the controls layout
    private void changeState(VidyoConnectorState state) {
        mLogger.Log("changeState: " + state.toString());

        mVidyoConnectorState = state;

        // Execute this code on the main thread since it is updating the UI layout.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Set the status text in the toolbar.
                mToolbarStatus.setText(mStateDescription.get(mVidyoConnectorState));

                // Depending on the state, do a subset of the following:
                // - update the toggle connect button to either start call or end call image: mToggleConnectButton
                // - display toolbar in case it is hidden: mToolbarLayout
                // - show/hide the connection spinner: mConnectionSpinner
                // - show/hide the input form: mControlsLayout
                switch (mVidyoConnectorState) {
                    case Connecting:
                        mToggleConnectButton.setChecked(true);
                        mConnectionSpinner.setVisibility(View.VISIBLE);
                        break;

                    case Connected:
                        mToggleConnectButton.setChecked(true);
                        mControlsLayout.setVisibility(View.GONE);
                        mConnectionSpinner.setVisibility(View.INVISIBLE);

                        // Keep the device awake if connected.
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        break;

                    case Disconnecting:
                        // The button just switched to the callStart image.
                        // Change the button back to the callEnd image because do not want to assume that the Disconnect
                        // call will actually end the call. Need to wait for the callback to be received
                        // before swapping to the callStart image.
                        mToggleConnectButton.setChecked(true);
                        break;

                    case Disconnected:
                    case DisconnectedUnexpected:
                    case Failure:
                    case FailureInvalidResource:
                        mToggleConnectButton.setChecked(false);
                        mToolbarLayout.setVisibility(View.VISIBLE);
                        mConnectionSpinner.setVisibility(View.INVISIBLE);

                        // If a return URL was provided as an input parameter, then return to that application
                        if (mReturnURL != null) {
                            // Provide a callstate of either 0 or 1, depending on whether the call was successful
                            Intent returnApp = getPackageManager().getLaunchIntentForPackage(mReturnURL);
                            returnApp.putExtra("callstate", (mVidyoConnectorState == VidyoConnectorState.Disconnected) ? 1 : 0);
                            startActivity(returnApp);
                        }

                        // If the allow-reconnect flag is set to false and a normal (non-failure) disconnect occurred,
                        // then disable the toggle connect button, in order to prevent reconnection.
                        if (!mAllowReconnect && (mVidyoConnectorState == VidyoConnectorState.Disconnected)) {
                            mToggleConnectButton.setEnabled(false);
                            mToolbarStatus.setText("Call ended");
                        }

                        if (!mHideConfig ) {
                            // Display the form.
                            mControlsLayout.setVisibility(View.VISIBLE);
                        }

                        // Allow the device to sleep if disconnected.
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        break;
                }
            }
        });
    }

    /*
     * Button Event Callbacks
     */

    @Override
    public void onClick(View v) {
//        if (mVidyoConnector != null) {
//            switch (v.getId()) {
//                case R.id.connect:
//                    // Connect or disconnect.
//                    this.toggleConnect();
//                    break;
//
//                case R.id.camera_switch:
//                    // Cycle the camera.
//                    mVidyoConnector.cycleCamera();
//                    break;
//
//                case R.id.camera_privacy:
//                    // Toggle the camera privacy.
//                    mCameraPrivacy = mCameraPrivacyButton.isChecked();
//                    mVidyoConnector.setCameraPrivacy(mCameraPrivacy);
//                    break;
//
//                case R.id.microphone_privacy:
//                    // Toggle the microphone privacy.
//                    mMicrophonePrivacy = mMicrophonePrivacyButton.isChecked();
//                    mVidyoConnector.setMicrophonePrivacy(mMicrophonePrivacy);
//                    break;
//
//                case R.id.toggle_debug:
//                    // Toggle debugging.
//                    mEnableDebug = !mEnableDebug;
//                    if (mEnableDebug) {
//                        mVidyoConnector.enableDebug(7776, "warning info@VidyoClient info@VidyoConnector");
//                        mClientVersion.setVisibility(View.VISIBLE);
//                    } else {
//                        mVidyoConnector.disableDebug();
//                        mClientVersion.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//
//                default:
//                    mLogger.Log("onClick: Unexpected click event, id=" + v.getId());
//                    break;
//            }
//        } else {
//            mLogger.Log("ERROR: not processing click event because Connector is null.");
//        }
    }

    // The Connect button was pressed.
    // If not in a call, attempt to connect to the backend service.
    // If in a call, disconnect.
    public void toggleConnect() {
//        if (mToggleConnectButton.isChecked()) {
//            // Connect to either a Vidyo.io resource or a VidyoCloud Vidyo room.
//            if (!mVidyoCloudJoin) {
//                // Connect to a Vidyo.io resource.
//
//                // Abort the Connect call if resource ID is invalid. It cannot contain empty spaces or "@".
//                String resourceId = mResourceId.getText().toString().trim(); // trim leading and trailing white space
//                if (resourceId.contains(" ") || resourceId.contains("@")) {
//                    this.changeState(VidyoConnectorState.FailureInvalidResource);
//                } else {
//                    this.changeState(VidyoConnectorState.Connecting);
//
//                    if (!mVidyoConnector.connect(
//                            mHost.getText().toString().trim(),
//                            mToken.getText().toString().trim(),
//                            mDisplayName.getText().toString().trim(),
//                            resourceId,
//                            this)) {
//                        // Connect failed.
//                        this.changeState(VidyoConnectorState.Failure);
//                    }
//                }
//            } else {
//                // Connect to a VidyoCloud Vidyo system, not Vidyo.io.
//                this.changeState(VidyoConnectorState.Connecting);
//
//                if (!mVidyoConnector.connectToRoomAsGuest(
//                        mPortal,
//                        mDisplayName.getText().toString().trim(),
//                        mRoomKey,
//                        mRoomPin,
//                        this)) {
//                    // Connect failed.
//                    this.changeState(VidyoConnectorState.Failure);
//                }
//            }
//            mLogger.Log("VidyoConnectorConnect status = " + (mVidyoConnectorState == VidyoConnectorState.Connecting));
//        } else {
//            // The user is either connected to a resource or is in the process of connecting to a resource;
//            // Call VidyoConnectorDisconnect to either disconnect or abort the connection attempt.
//            this.changeState(VidyoConnectorState.Disconnecting);
//            mVidyoConnector.disconnect();
//        }
    }

    // Toggle visibility of the toolbar
    @Override
    public void onVideoFrameClicked() {
        if (mVidyoConnectorState == VidyoConnectorState.Connected) {
            if (mToolbarLayout.getVisibility() == View.VISIBLE) {
                mToolbarLayout.setVisibility(View.INVISIBLE);
            } else {
                mToolbarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     *  Connector Events
     */

    // Handle successful connection.
    @Override
    public void onSuccess() {
        mLogger.Log("onSuccess: successfully connected.");
        this.changeState(VidyoConnectorState.Connected);
    }

    // Handle attempted connection failure.
    @Override
    public void onFailure(Connector.ConnectorFailReason reason) {
        mLogger.Log("onFailure: connection attempt failed, reason = " + reason.toString());
        this.changeState(VidyoConnectorState.Failure);
    }

    // Handle an existing session being disconnected.
    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason reason) {
        if (reason == Connector.ConnectorDisconnectReason.VIDYO_CONNECTORDISCONNECTREASON_Disconnected) {
            mLogger.Log("onDisconnected: successfully disconnected, reason = " + reason.toString());
            this.changeState(VidyoConnectorState.Disconnected);
        } else {
            mLogger.Log("onDisconnected: unexpected disconnection, reason = " + reason.toString());
            this.changeState(VidyoConnectorState.DisconnectedUnexpected);
        }
    }
    @Override
    public void onChatMessageReceived(Participant participant, ChatMessage message){

    }
    // Handle local camera events.
    @Override
    public void onLocalCameraAdded(LocalCamera localCamera) {
        mLogger.Log("onLocalCameraAdded: " + localCamera.getName());
    }

    @Override
    public void onLocalCameraRemoved(LocalCamera localCamera) {
        mLogger.Log("onLocalCameraRemoved: " + localCamera.getName());
    }

    @Override
    public void onLocalCameraSelected(LocalCamera localCamera) {
        mLogger.Log("onLocalCameraSelected: " + (localCamera == null ? "none" : localCamera.getName()));

        // If a camera is selected, then update mLastSelectedCamera.
        if (localCamera != null) {
            mLastSelectedCamera = localCamera;
        }
    }

    @Override
    public void onLocalCameraStateUpdated(LocalCamera localCamera, Device.DeviceState state) {
        mLogger.Log("onLocalCameraStateUpdated: name=" + localCamera.getName() + " state=" + state);
    }

    // Handle a message being logged.
    @Override
    public void onLog(LogRecord logRecord) {
        // No need to log to console here, since that is implicitly done when calling registerLogEventListener.
    }

    // Handle network interface events
    @Override
    public void onNetworkInterfaceAdded(NetworkInterface vidyoNetworkInterface) {
        mLogger.Log("onNetworkInterfaceAdded: name=" + vidyoNetworkInterface.getName() + " address=" + vidyoNetworkInterface.getAddress() + " type=" + vidyoNetworkInterface.getType() + " family=" + vidyoNetworkInterface.getFamily());
    }

    @Override
    public void onNetworkInterfaceRemoved(NetworkInterface vidyoNetworkInterface) {
        mLogger.Log("onNetworkInterfaceRemoved: name=" + vidyoNetworkInterface.getName() + " address=" + vidyoNetworkInterface.getAddress() + " type=" + vidyoNetworkInterface.getType() + " family=" + vidyoNetworkInterface.getFamily());
    }

    @Override
    public void onNetworkInterfaceSelected(NetworkInterface vidyoNetworkInterface, NetworkInterface.NetworkInterfaceTransportType vidyoNetworkInterfaceTransportType) {
        mLogger.Log("onNetworkInterfaceSelected: name=" + vidyoNetworkInterface.getName() + " address=" + vidyoNetworkInterface.getAddress() + " type=" + vidyoNetworkInterface.getType() + " family=" + vidyoNetworkInterface.getFamily());
    }

    @Override
    public void onNetworkInterfaceStateUpdated(NetworkInterface vidyoNetworkInterface, NetworkInterface.NetworkInterfaceState vidyoNetworkInterfaceState) {
        mLogger.Log("onNetworkInterfaceStateUpdated: name=" + vidyoNetworkInterface.getName() + " address=" + vidyoNetworkInterface.getAddress() + " type=" + vidyoNetworkInterface.getType() + " family=" + vidyoNetworkInterface.getFamily() + " state=" + vidyoNetworkInterfaceState);
    }
}
