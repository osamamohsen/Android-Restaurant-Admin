# Simple-Android-Restaurant-Admin

### Android Restaurant Client Side
https://github.com/osamamohsen/Android-Restaurant

### Restaurant Android application which makes use of the following Google Firebase components:

    Database
    Storage


### Still a work in progress: Requires:

    Add edit operation
    Add Google Authentication
    Bug fix for window leak due to progress bar.
    Code refactor.
    Enable off line mode (requires additional configuration for picasso.
    Page Recycler view and optimize it.

### To Run the project.

    Navigate to the Firebase Console.
    Create a new project.
    Click the "Add Firebase to your android app.
    change Firebase of Database->Rules	{
	  "rules": {
	    ".read": true,
	    ".write": true
	  }
