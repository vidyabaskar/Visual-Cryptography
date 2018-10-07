# Visual-Cryptography

A secure authentication technique that uses two (shared) images for authentication process.
  -The algorithm generates an image for the user credentials when the user is signing up.
  -This image is used for generating the first shared image.
  -Again when the user tries to login into the system, first shared image and the image generated with the credentials are used to generate the second shared image.
   -Overlapping these two (first and second shared) images, the user credentials will be revealed.
   -These credentials are further identified by OCR and then the credentials are used for authorizing the user.
