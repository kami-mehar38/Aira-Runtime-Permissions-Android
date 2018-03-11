# Aira-Runtime-Permissions-Android
Helping the android developers to quickly go for the runtime permissions without any bulk of code and headache.
What differs this library
* Short Code
* Permission within one method
* Easy to implement
* Callbacks to check for the permission status

## Demo

![screenshot_1520791891](https://user-images.githubusercontent.com/19648192/37256829-f663a880-2581-11e8-9d55-fa69c37a2054.png)

![screenshot_1520791907](https://user-images.githubusercontent.com/19648192/37256838-1e02bb4c-2582-11e8-8a6f-6dfde1e078b0.png)

![screenshot_1520791926](https://user-images.githubusercontent.com/19648192/37256850-316125ca-2582-11e8-8e17-17cbc148794e.png)

### Installing with Gradle

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.kami-mehar38:Aira-Runtime-Permissions-Android:1.0.1'
	}

### Installing with Maven

Step 1. Add the JitPack repository to your build file

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  
Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.kami-mehar38</groupId>
	    <artifactId>Aira-Runtime-Permissions-Android</artifactId>
	    <version>1.0.1</version>
	</dependency>

## Example

Simply go for the following method to check if the permissions is granted or not, in this method has two overridden methods that are essential to listen for the permission callbacks.

    Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                            PERMISSION_CONSTANT_CAMERA, "Need Permissions", "This app needs multiple permissions to work properly, grant the permission if you want to get all the features.",
                            new Aira.OnPermissionResultListener() {
                            
                                @Override
                                public void onPermissionGranted() {
                                    // Permission Granted
                                }

                                @Override
                                public void onPermissionFailed() {
                                    // Permission not granted
                                }
                            });
                            
                            
Send the activity result to the Aira library to check for the permission result, in your onActivityResult and onRequestPermissionsResult method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Aira.onActivityResult(requestCode);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Aira.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

## Contributing

Please read [CONTRIBUTING.md](https://github.com/kami-mehar38/Aira-Runtime-Permissions-Android) for details on our code of conduct, and the process for submitting pull requests to us.


## Authors

* **Kamran Ramzan** - *Full Project* - [kami-mehar38](https://github.com/kami-mehar38)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/kami-mehar38/Aira-Runtime-Permissions-Android/blob/master/LICENSE) file for details

## Acknowledgments
* Inspiration www.androidhive.info
