# KBox
[![Build Status](https://travis-ci.org/AKSW/KBox.svg?branch=master)](https://travis-ci.org/AKSW/KBox)
[![Coverage Status](https://coveralls.io/repos/github/AKSW/KBox/badge.svg?branch=master)](https://coveralls.io/github/AKSW/KBox?branch=master)
[![Join the chat at https://gitter.im/AKSW/DeFacto](https://badges.gitter.im/AKSW/KBox.svg)](https://gitter.im/AKSW/KBox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

KBox is an abbreviation for Knowledge Box. 
The rationality behind KBox is to allow users to have a single place to share resources and knowledge among different applications as well as instances. 
Moreover, working on top of RDF model, KBox is a natural extension of the Web on your computer.

- [Why use KBox?](https://github.com/AKSW/KBox#why-use-kbox)
- [What is possible to do with it?](https://github.com/AKSW/KBox#what-is-possible-to-do-with-it)
- [How can I use KBox?](https://github.com/AKSW/KBox#how-can-i-use-kbox)
- [How can I execute KBox in command Line?](https://github.com/AKSW/KBox#how-can-i-execute-kbox-in-command-line)
- [How can I use KBox in my project?](https://github.com/AKSW/KBox#how-can-i-use-kbox-in-my-project)
- [How can I query a published knowledge graph?](https://github.com/AKSW/KBox#how-can-i-query-a-published-knowledge-graph)
- [How can I query multi-graphs?](https://github.com/AKSW/KBox#how-can-i-query-multi-graphs)
- [How can I publish my own dataset?](https://github.com/AKSW/KBox#how-can-i-publish-my-own-dataset)

### Why use KBox?
Applications usually deal with resources and knowledge that are often duplicated among several applications.
For instance, when using Stanford NLP library among different applications, the resources and knowledge inside the library is duplicated among different instances.
The idea is to have a common knowledge box where users can persist and yet retrieve and share resources without duplication.
In order to do that, we bring the RDF concept to KBox.
Thereafter, resource can be uniquely identified.

### What is possible to do with it?
With KBox you can share resources and knowledge among several applications, but not just that.
In order to allow the easier knowledge dissemination, we have implemented Kibe library.
The Kibe library allows applications to virtually install and query RDF knowledge graphs.
It takes around ~50 minutes to start quering DBpedia on your computer, avoiding server overheads and faults.

### How can I use KBox?
You can use KBox in command line or the library on your application.
It is easy to plug and use it.

### How can I execute KBox in command Line?

* Download the library [here](https://github.com/AKSW/KBox/releases).

* Type the following:
```
java -jar kbox.jar <command> [option]
Where [command] is:
* -createIndex <directory> - Create an index with the files in a given directory.
 ps: the directory might contain only RDF compatible file formats.
* -sparql <query> -graph <graph>  - Query a given graph.
* -kns-list  - List all availables KNS services.
* -kns-install <kns-URL>  - Install a given KNS service.
* -kns-remove <kns-URL>  - Remove a given KNS service.
* -r-install  <URL>  - Install a given resource in KBox.
* -kb-install  <kb-URL> - Install a given knowledge base using the available KNS services to resolve it.
* -kb-install  <kb-URL> -index <indexFile> - Install a given index in a given knowledge base URL.
* -kb-install  <kb-URL> -kns-server <kns-server-URL> - Install a knowledge base from a a given KNS server.
* -kb-list  - List all available KNS services and knowledge bases.
* -r-dir <resourceDir> - Change the current path of the KBox resource directory.
* -version - display KBox version.
```

### How can I use KBox in my project?

* KBox is distributed over Maven.
* You can add KBox to your project by importing the desired library:

1) Add the following dependency on your project:
```
<dependency>
  <groupId>org.aksw.kbox</groupId>
  <artifactId>kbox.core</artifactId>
  <version>0.0.1-alpha1</version>
</dependency>
```
2) Add the internal AKSW repository on your pom file:
```
<repositories>
    <repository>
      <id>maven.aksw.internal</id>
      <name>University Leipzig, AKSW Maven2 Repository</name>
      <url>http://maven.aksw.org/archiva/repository/internal</url>
    </repository>
  ...
</repositories>
```
3) Rock it.. ;-)

### How can I query a published knowledge graph?

Weeeelll.. its quite easy.
Remember the commands listed on '[How can I execute KBox in command Line](https://github.com/AKSW/KBox#how-can-i-execute-kbox-in-command-line)'.
So its just execute the command line bellow (remember to add -install, so the knowledge graph is automatically derefereced):

```
java -jar kbox-v0.0.1-alpha2.jar -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}" -graph "https://www.w3.org/2000/01/rdf-schema" -install
------
| n  |
======
| 32 |
------
```

### How can I query multi-graphs?

Its very easy, you just need to add the knowledge graph that you want to query separated by comma as the command bellow:

In the given example, we query two knowledge graphs https://www.w3.org/2000/01/rdf-schema and http://xmlns.com/foaf/0.1.
```
java -jar kbox-v0.0.1-alpha2.jar -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}" -graph "https://www.w3.org/2000/01/rdf-schema,http://xmlns.com/foaf/0.1" -install
-------
| n   |
=======
| 123 |
-------
```


### How can I publish my own Dataset?

Although you can create your own KNS service and publish your Datasets, currently KBox does not allow you to directly publish content to other users.
In order to do that, you should contact us.

Contact: 
cbaron@informatik.uni-leipzig.de

Information needed:

1) Dataset Label;

2) Dataset Version;

3) The Publisher: Your or your organization's email/URL;

4) The Creator: Who has created the Knowledge base e.g. DBpedia -> http://dbpedia.org;

5) The License: the dataset license;

6) The URL where the knwoledge graph file can be dereferenced (please create the file using KBox createIndex command);

7) The Dataset URI name: the URI name that will be used by users to dereference your dataset;

8) The Dataset description: Give us a few words to help others to know what your dataset is about;

9) Tell us one reason why KBox is awesome. :-)
