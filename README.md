RockLobster
===========

A simple static Blog generator for Git-hosted Markdown (plus metadata) files.

It takes files like:

		tags: random, story
		title: And here's a story about... being free
		date: 2013-01-01 01:01:01 +500
		some-other-metadata: hiya
		
		Some Markdown text here

And creates pages like

		myblogpost.html, index.html, tags_YOURTAG.html, tags_YOURTAG.2.html, sitemap.xml, etc.

automatically via Git hooks.

More functionality is supported through plugins.

It is currently running http://blog.denevell.org/

Features
========

* Automatically fetches posts from your git repository
* Automatically updates your blog (via git hooks)
* Page pagintion for index.html etc, and filtered pagination (e.g 'tags_stories.2.html')
* Manipulate the posts and metadata using the Mustache templating language and plugins.
* Automatically adds dates as the last git commit date, unless 'date' metadata exists
* Fuzzy date metadata matching in the posts (using jchronic)
* Comments via disqus (edit the JS in the template files)
* Google Analytics - add your account id in the default template.
* Includes Bootstrap
* Sitemap support
* RSS (Really Atom) support 
* Plugins:
 * Tags (for single post and entire blog)
 * Pretty dates

Running (from the repository)
=============================

1. Install gradle (apt-get install gradle, homebrew install gradle or http://www.gradle.org/installation).
2. Create the rock.lobster configuration file:
       
               git_repo    = https://github.com/denevell/BlogPosts.git
               output_dir  = somedirectory/ 
               # optional
               file_suffix = .md

3. gradle clean runJar

You also need to have singepages.template and pagination.template.your_suffix (actually optional) template files in your working directory. See the examples in this repository.

Then the HTML output files, single pages and paginated pages, will appear in your output directory. The disqus comments will only work when the files are hosted online.

Running (from the packaged jar)
=============================

Once you've done the above, you can run 'gradle zip' to make a packaged version of the jar, resources, config file and template files.

You can then distribute this and simply run 'java -jar build/libs/RockLobster.jar' to create your blog.

Specifying the template files
=============================

There are example templates in the repository.

Single page template
--------------------

**'singlepages.template.YOUR_SUFFIX'** - Required file.

The template file uses the Mustache templating syntax. See the example file in this repository. Here is a sample template where you access the title metadata in your markdown file and the post content:

        	{{basefilename}} // I.e. If you markdown file was sup.md it would be 'sup'.
        	{{#attr}}{{title}}{{/attr}}
        	{{& post}}

The YOUR_SUFFIX part will normally be .html, but you can change this.
            
Paginated page template
-----------------------

**'SOMENAME.10.pagination.template.YOUR_SUFFIX'** - The number refers to how many post per page.

It will create a paginated page for all your posts with 10 pages on each page. This also includes metadata pertaining the pagination (see the example). Within your template file, your Mustache syntax will look like:

		{{#posts}}
			<div class="blog-posts-entry-title">
				{{#metadata}}{{& title}}{{/metadata}}
			</div>
			<div class="blog-posts-entry-post">
				{{& post}}
			</div>
		{{/posts}}
		...
		Page {{num_pages_current}} of {{num_pages_total}}
		{{#next_page_relative_url}}<a href="{{next_page_relative_url}}">Next</a>{{/next_page_relative_url}} 
		{{#previous_page_relative_url}}<a href="{{previous_page_relative_url}}">Previous</a>{{/previous_page_relative_url}}

With the {{#posts}} {{/posts}} block you can put in everything you put in the single page template above.

The YOUR_SUFFIX part will normally be .html, but you can change this.

Filtered paginated page template
--------------------------------

**'SOMENAME_[metadata-key].10.pagination.template.YOUR_SUFFIX'** - This creates multiple paginated pages. [metadata-key] related to all the values of that metadata key.

In the case where you have [tags] in the filename, and you have blogpost metadata which contains the tags 'stuff' and 'blar', you'd generated 'SOMENAME_stuff.html' and 'SOMENAME_blar.html'. 

And in the templates {{metadata_filter}} would refer to 'stuff' and 'blar' respectively.

The YOUR_SUFFIX part will normally be .html, but you can change this.

Resources
=======

All the files you place in resources/ will be places in YOUR-OUTPUT-DIR/resources/.

Bootstap is included.

Sitemap
=======

See the sample sitemap.500.pagination.template.xml for an example template file that will generate your sitemap.

The 500.pagination part is so we'll get all the posts (unless you have more than 500) in the template.

RSS / (Really Atom)
=======

See the sample atom.500.pagination.template.xml for an example template file that will generate your feed.

The 500.pagination part is so we'll get all the posts (unless you have more than 500) in the template.

Plugins
=======

Most things - i.e. anything other than processing a single markdown-with-metadata file, paginated files and filtered paginated files  - are achieved through plugins.

In the templates you can specify:

		{{#plugins}}TAGNAME||argument-1||argument-2{{/plugins}}

The TAGNAME will be a registered plugin, the the rest of the line will be the arguments, separated by a double vertical bar.

All the plugins live in org.denevell.rocklobster.plugins and any classes which extend Plugin will be automatically added to the list of plugins available. TODO: Ability to add your own plugin easily.

'all-tags' plugin
---------------

This gets all the 'tags' metadata from all the blog posts and outputs them.

* Arguments 1 and 2: beginning and end of the wrapper around the whole block.
* Arguments 3 and 4: beginning and end of the wrapper around each tag
* Argument 5: The divider inbetween each element.
* Argument 6: Number to add to the occurrences number, which is accessbile using [occurrences] in argument 3.

At argument 3, any text with [tagname] is replaced for the current tag name, and [occurrences] is replaces for the occurrences of the tagname.

E.g:

                {{#plugins}}all-tags||<div>||</div>||<a href="category_[tagname].html">||</a>|| &#124; ||13{{/plugins}}

'single-page-tags' plugin
---------------

The outputs the tags that are passed into it, but formatted using the arguments

* Argument 1: the tags. Usually found via {{#attr}}{{tags}}{{/attr}}
* Arguments 2 and 3: beginning and end of the wrapper around the whole block.
* Arguments 4 and 5: beginning and end of the wrapper around each tag
* Argument 6: The divider inbetween each element.

At argument 4, any text with [tagname] is replaced for the current tag name.

E.g:

		{{#plugins}}single-page-tags||{{#attr}}{{tags}}{{/attr}}||<div>||</div>||<a href="category_[tagname].html">||</a>|| &#124; {{/plugins}}

'pretty-date' plugin
---------------

This uses the jchronic natural language parsing algorithm to convert text to a date. 

* Argument 1: the date string, usually {{#attr}}{{date}}{{/attr}}.
* Argument 2: the date format string for output, conforming to http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html 

		Posted: {{#plugins}}pretty-date||{{#attr}}{{date}}{{/attr}}||EEEE d MMMM yyyy, h:ma{{/plugins}}

Automatic publishing
====

If you setup your web server to run the Java jar file as a cgi binary, then you can use git hooks to automatically recreate your blog on each new commit.

1. Setup your HTTPD server to accept cgi binary files. (See [this](http://httpd.apache.org/docs/2.2/howto/cgi.html) for apache)
2. Place the jar file with (the 'rock.lobster' configuration file and the template files) in a directory and create a **executable** shell script in the same directory:

		#!/bin/sh
		
		JAVA="/usr/bin/java"
		$JAVA -jar RockLobster.jar

3. Ensure this directory has write permissions for the HTTPD user (www-data in Debian's case).
4. Create a git hook on the location of your markdown repository to call this binary.  	

Disqus comments
===

There is example disqus javascript in singlepage.template. You must change the 'disqus_shortname' variable to your registered disqus account.

Alternatively remove all that disqus javascript and start afresh using [these](http://disqus.com/admin/universalcode/) instructions.

Release plan
====

0.8
* ~~Sort blog posts by git date or metadata date~~
* ~~Specify blog url from the command line~~
* ~~Specify where to put the output html post files~~
* ~~Remove .md from output html filename~~
* ~~Optional template files found in CWD to use.~~
* ~~Capitalisation in blog attributes - make them all lowercase on parsing?~~
* ~~Ant build.xml / Gradle? - sod Eclipse~~
* ~~Pagination for index.html etc~~
* ~~Pagination filter based on blog post attributes - category_[metadata.tags].15.pages.template ?~~
* ~~Turn off pagination when specified as 0 - just set it really high, the first page will be just index.html anyway.~~
* ~~Integrate disqus?~~
* ~~Plugins~~
 * ~~Tags plugin~~
 * ~~Plugin: Pretty date~~
 * ~~Plugin: Single post tags~~
 * ~~Ability to easily add a new plugin~~
* ~~Log problems especially for problems parsing the date with jchronic, log4j?~~

0.9
* ~~Allow '.', and '..' for the output directory.~~
* ~~Automatically call binary from github hooks~~
* ~~Better disqus integration instructions~~
* ~~Gradle task to run the jar after compilation~~
* Instructions (plus dev package?) for plugin development.
 * Upload to mvnrepository?
* ~~Specify directory containing css etc files for the output directory~~
* ~~Only parse .md files -- configurable~~
* Version number
* ~~Analytics~~
* ~~Sitemap~~
 * ~~Ability to make change a .template file to whatever you want~~
 * ~~Add the base url to rock.lobster - just edit the sitemap.500.template.xml template file~~
* ~~Specify output postfix, currently .html~~
* ~~RSS template~~

1.0
* Allow markdown files to be in sub folders
* Themes
* ~~Create a bundle of jar file, configuration file, themes~~
 * ~~Gradle zip task~~
* Upload a bundle to github with version number
* Find out why unicode characters in the .template files are munged when parsed

1.0.x
* Turn off looking for git repo updates
* Better configuration-file / template names error reporting
* Specifying leading and ending text for text around paginated number in filename
* Allow git commit date to be the first commit data, not the latest

1.x
* Compositing so there's a master template file which would contain either posts or paginated content?
* Refactor blog parsing code to be less procedural
* Support YAML metdata to help converting from Jekyll?
* Allow user to specify values in the config file that they can access in the templates

Plugins:
* Plugin: Content abbreviator for index.html posts
* Sort tags alphabetically in tags plugins
