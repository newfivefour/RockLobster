RockLobster
===========

Blog generator for Markdown files on Github (Hacked up)

Run 'ant' to get all the dependencies (all 4.2MB of them...) from maven via Apache Ivy (which you also need to have installed).

Then just run the program as normal Java file, and it will create HTML pages for the markdown files on my blog.

TODO
====
* Sort blog posts by git date or metadata date
* ~~Specify blog url from the command line~~
* ~~Specify where to put the output html post files~~
* ~~Remove .md from output html filename~~
* Optional template files found in CWD to use.
* Ant build.xml / Gradle? - sod Eclipse
* Plugins 
* How to do pagination in index files and possibly others
  * Via a plugin? Metadata in filename - index.5.pages.template?
