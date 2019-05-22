Recover files from a SD card:
  (1) use dd to create an image of the sd card with accidentally
      deleted images (replace "ty-sd-card-recovery-project" with
      your desired filename):
        sudo dd if=/dev/disk2s1 of=ty-sd-card-recovery-project
  (2) create an directory somewhere to hold the output 
      (I used /Users/mooreb/Desktop/ty recovery/)
  (3) replace "/Users/mooreb/Desktop/ty recovery/" in Main.java
      with the output directory from (2)
  (4) in Main.java, replace 
        "/Users/mooreb/Desktop/ty-sd-card-recovery-project" 
      with the full path of your desired filename (from (1) above)
  (5) look at and/or remove 4685318144L in Main.java; there weren't
      any pictures after a certain point on the sd card
      (this saved time)
  (6) run Main#main

The dd required a bunch of time (I didn't carefully watch, maybe
12 hours for a 32 GiB card?)

This program took a couple hours to write, and about 30 seconds to run.

Writing this "saved" us $40 from san disk/lc-tech RescuePRO and/or
PHOTORECOVERY 2019. I ran those programs to see how they could do;
they detected fewer images, took a longer time (~3 days) to run,
and only previewed < 100 images. We would have gladly spent the $40
but it wasn't clear that it would pay off any better than my naive
attempt here.

Using this program I was able to recover ~240 of 1013 pictures.
The remainder were either partially corrupt or completely unreadable.

Things this program does not do
  * Fully parse the extensively complicated (and apparently not
    publicly documented (grumble $95 ANSI standard grumble) JPEG
    standard (I relied on Ty's camera FUJIFILM Digital Camera FinePix
    XP130 XP131 XP135 Ver1.00) writing the Exif tag very early in the
    JPEG/TIFF stream). Reading libjpeg's rdjpegcom.c was a huge help.
  * Attempt to recover video
  * Deal with fragmented files in any way (this code assumes that all
    pictures are contiguous)
  * Attempt to rebuild the file index
  * Deal with any filesystem metadata
  * Deal with any image metadata
