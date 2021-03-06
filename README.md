# Aira-Runtime-Permissions-Android
Helping the android developers to quickly go for the runtime permissions without any bulk of code and headache.
What differs this library

## Whats new
A separate method to check if the permission is granted or not

* Short Code
* Permission within one method
* Easy to implement
* Callbacks to check for the permission status
* Method to check for the permission grant status

## Demo

![ezgif com-resize 1](https://user-images.githubusercontent.com/19648192/37361937-d5c53d24-2715-11e8-9d48-c739404e5f45.gif)

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
	        implementation 'com.github.kami-mehar38:Aira-Runtime-Permissions-Android:1.1.2'
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
	    <version>1.1.2</version>
	</dependency>

## Example

Simply go for the following method to check if the permissions is granted or not, in this method has two overridden methods that are essential to listen for the permission callbacks.

### Checking for the permission

	if (Aira.checkPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA})) {
		// permission is already granted
	} else {
		// permission is not granted
	}

### Requesting multiple permissions

    Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                    PERMISSION_CONSTANT_ALL, "Need Permissions", "This app needs multiple permissions to work properly, grant the permission if you want to get all the features.",
                    new Aira.OnPermissionResultListener() {
                        @Override
                        public void onPermissionGranted(List<String> grantedPermissions) {
                            
                        }

                        @Override
                        public void onPermissionFailed(List<String> failedPermissions) {
                            
                        }
                    });


### Requesting single permission

    Aira.requestPermission(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CONSTANT_CAMERA, "Camera Permission", "This app needs camera permission to work properly, grant the permission if you want to get all the features.",
                    new Aira.OnPermissionResultListener() {
                        @Override
                        public void onPermissionGranted(List<String> grantedPermissions) {
                            
                        }

                        @Override
                        public void onPermissionFailed(List<String> failedPermissions) {
                        
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

## License

MIT License

Copyright (c) 2018 Kamran Ramzan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Acknowledgments
* Inspiration www.androidhive.info
