# KBox
[![Build Status](https://travis-ci.org/AKSW/KBox.svg?branch=master)](https://travis-ci.org/AKSW/KBox)
[![Coverage Status](https://coveralls.io/repos/github/AKSW/KBox/badge.svg?branch=master)](https://coveralls.io/github/AKSW/KBox?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2beb954156c2410d8a05a0e7f36d62d4)](https://www.codacy.com/app/marx/KBox?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AKSW/KBox&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/33afd9bc-cc14-4fcc-bd3d-c1f57ed35190)](https://codebeat.co/projects/github-com-aksw-kbox)
[![Join the chat at https://gitter.im/AKSW/KBox](https://badges.gitter.im/AKSW/KBox.svg)](https://gitter.im/AKSW/KBox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Project Stats](https://www.openhub.net/p/knowledge-box/widgets/project_thin_badge.gif)](https://www.ohloh.net/p/knowledge-box)

```
@InProceedings{Marx/KBox/ICSC/2017,
  Title                    = {{KBox}: {T}ransparently {S}hifting {Q}uery {E}xecution on {K}nowledge {G}raphs to the {E}dge},
  Author                   = {Edgard Marx and Ciro Baron and Tommaso Soru and S\"oren Auer},
  Booktitle                = {11th IEEE International Conference on Semantic Computing, Jan 30-Feb 1, 2017, San Diego, California, USA},
  Year                     = {2017}
}
```

KBox is an abbreviation for Knowledge Box. 
The rationale behind KBox is to allow users to have a single place to share resources and knowledge among different applications as well as instances.
Moreover, working on top of RDF model, KBox is a natural extension of the Web on your computer.

- [Why use KBox?](https://github.com/AKSW/KBox#why-use-kbox)
- [What is possible to do with it?](https://github.com/AKSW/KBox#what-is-possible-to-do-with-it)
- [How can I use KBox?](https://github.com/AKSW/KBox#how-can-i-use-kbox)
- [How can I execute KBox in command Line?](https://github.com/AKSW/KBox#how-can-i-execute-kbox-in-command-line)
- [How can I use KBox in my project?](https://github.com/AKSW/KBox#how-can-i-use-kbox-in-my-project)
- [How can I list available knowledge bases?](https://github.com/AKSW/KBox#how-can-i-list-available-knowledge-bases)
- [How can I query a published knowledge bases?](https://github.com/AKSW/KBox#how-can-i-query-a-published-knowledge-bases)
- [How can I query multi-bases?](https://github.com/AKSW/KBox#how-can-i-query-multi-bases)
- [Starting a SPARQL enpoint](https://github.com/AKSW/KBox/blob/master/README.md#starting-a-sparql-endpoint)
- [Querying a SPARQL endpoint](https://github.com/AKSW/KBox/blob/master/README.md#querying-a-sparql-endpoint)
- [Listing the resource's folder](https://github.com/AKSW/KBox/blob/master/README.md#listing-the-resource-folder)
- [Changing the resource's folder](https://github.com/AKSW/KBox/blob/master/README.md#changing-the-resource-folder)
- [Using KBox with Docker](https://github.com/AKSW/KBox#using-kbox-with-docker)
- [How can I publish my own dataset?](https://github.com/AKSW/KBox#how-can-i-publish-my-own-dataset)
- [Check our Wiki for old APIs](https://github.com/AKSW/KBox/wiki)

### Why use KBox?
Systems usually deal with resources and knowledge that are often duplicated among several instances.
For instance, when using the Stanford NLP library the resources and knowledge inside the library are duplicated among different applications.
The idea is to have a common repository where users can share resources without duplication.
In order to do that, we bring the RDF concept to bridge the gap among reource publishig, storing and locating.

### What is possible to do with it?
With KBox you can share resources and knowledge among several applications, but not just that.
In order to allow an easier knowledge dissemination, we have implemented Kibe library.
The Kibe library allows applications to virtually install and query RDF knowledge bases.
It takes around ~50 minutes to start quering DBpedia on your computer to avoid server overheads and faults.

### How can I use KBox?
You can use KBox either as a command-line program or a library in your application.
It is easy to plug and use it.

### How can I execute KBox in command Line?

* Download the library [here](https://github.com/AKSW/KBox/releases).

* Type the following:
```
java -jar kbox.jar <command> [option]
KBox.jar <command> [option]
Where [command] is:
   * -createIndex <directory>    - Create an index with the files in a given directory.
                                 ps: the directory might contain only RDF compatible file formats.
   * -serialize <directory>      - Serialize the content of a directory to be served in a KNS system.
   * -sparql <query> (-kb <KB> | -server <URL>) [-install] [-json]       - Query a given KB (e.g. -sparql "Select ..." -kb "KB1,KB2")
                                                 - ps: use -install in case you want to enable the auto-dereference.
   * -server [-port <port> (default 8080)] [-subDomain <subDomain> (default kbox)] -kb <KB> [-install]   - Start an SPARQL enpoint in the given subDomain containing the given KB.
   * -list       - List all available knowledge base.
   * -list -kns  - List all availables KNS services.
   * -install <URL>      - Install a given resource.
   * -install -kns <kns-URL>     - Install a given KNS service.
   * -install -kb <kb-URL>       - Install a given knowledge base using the available KNS services to resolve it.
   * -install -kb <kb-URL> -format <format>      - Install a given knowledge base using the available KNS services to resolve it.
   * -install -kb <kb-URL> -format <format> -version <version>   - Install a given knowledge base using the available KNS services to resolve it.
   * -install -kb <kb-URL> -index <indexFile>    - Install a given index in a given knowledge base URL.
   * -install -kb <kb-URL> -kns <kns-URL>        - Install a knowledge base from a a given KNS service.
   * -install -kb <kb-URL> -kns <kns-URL> -format <format> -version <version> - Install a knowledge base from a a given KNS service with the specific format and version.
   * -remove -kns <kns-URL>      - Remove a given KNS service.
   * -info <kb-URL>      - Gives the information about a specific KB.
   * -info <kb-URL> -format <format>     - Gives the information about a specific KB.
   * -info <kb-URL> -format <format> -version <version>  - Gives the information about a specific KB.
   * -locate <URL>       - returns the local address of the given resource.
   * -locate -kb <kb-URL>        - returns the local address of the given KB.
   * -locate -kb <kb-URL> -format <format>       - returns the local address of the given KB.
   * -locate -kb <kb-URL> -format <format> -version <version>    - returns the local address of the given KB.
   * -search <kb-URL-pattern>    - Search for all kb-URL containing a given pattern.
   * -search <kb-URL-pattern> -format <format>   - Search for all kb-URL containing a given pattern.
   * -search <kb-URL-pattern> -format <format> -version <version>        - Search for all kb-URL containing a given pattern.
   * -r-dir      - Show the path to the resource folder.
   * -r-dir <resourceDir>        - Change the path of the resource folder.
   * -version    - display KBox version.
```

### How can I use KBox in my project?

* KBox is distributed over Maven.
* You can add KBox to your project by importing the desired library:

1) Add the following dependency on your project:
```
<dependency>
    	<groupId>org.aksw.kbox</groupId>
    	<artifactId>kbox.kibe</artifactId>
    	<version>v0.0.1-alpha3-RC16</version>
</dependency>
```
2) Add the internal AKSW repository to your pom file:
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

### How can I list available knowledge bases?

You can list the available knowledge graphs with list command:

```
java -jar kbox-v0.0.1-alpha3-RC16.jar -list
Knowledge base table list
http://dbpedia.org/3.9/en/full
http://dbpedia.org/3.9/en
http://dbpedia.org/2015-10/en
http://dbpedia.org/2015_10/en/full
http://dbpedia.org/2015-10/en/full
http://dbpedia.org/ontology
http://www.w3.org/1999/02/22-rdf-syntax-ns
https://www.w3.org/2000/01/rdf-schema
...
```

### How can I query a published knowledge base?

Weeeelll... it's quite easy.
Remember the commands listed on '[How can I execute KBox in command Line](https://github.com/AKSW/KBox#how-can-i-execute-kbox-in-command-line)'.
It's just about executing the command line below. Remember to add `-install`, so the knowledge base is automatically dereferenced.

```
java -jar kbox-v0.0.1-alpha3-RC16.jar -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}" -kb "https://www.w3.org/2000/01/rdf-schema" -install
------
| n  |
======
| 32 |
------
```

Or using the java API...

```
KBox.query("Select (count(distinct ?s) as ?n) where {?s ?p ?o}", true, new URL("https://www.w3.org/2000/01/rdf-schema"));
```

You might want to setup the model before starting to execute multiple queries on it:

```
Model model = KBox.createModel(new URL("https://www.w3.org/2000/01/rdf-schema"));
KBox.query("Select (count(distinct ?s) as ?n) where {?s ?p ?o}", model);
```

### How can I query multi-bases?

It's very easy, as you just need to add the knowledge base you want to query separated by commas as the command below:

In the given example, we query two knowledge bases, https://www.w3.org/2000/01/rdf-schema and http://xmlns.com/foaf/0.1.
```
java -jar kbox-v0.0.1-alpha3-RC16.jar -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}" -kb "https://www.w3.org/2000/01/rdf-schema,http://xmlns.com/foaf/0.1" -install
-------
| n   |
=======
| 123 |
-------
```
Or using the java API...
```
KBox.query("Select (count(distinct ?s) as ?n) where {?s ?p ?o}", 
                   true, new URL("https://www.w3.org/2000/01/rdf-schema"), 
                   new URL("http://xmlns.com/foaf/0.1"));
```
You might want to setup the model before starting to execute multiple queries on it:
```
Model model = KBox.createModel(new URL("https://www.w3.org/2000/01/rdf-schema"),
                   new URL("http://xmlns.com/foaf/0.1"));
KBox.query("Select (count(distinct ?s) as ?n) where {?s ?p ?o}", model);
```

### Starting a SPARQL endpoint

Yes, you can!!

```
java -jar kbox-v0.0.1-alpha3-RC16.jar -server -kb "https://www.w3.org/2000/01/rdf-schema,http://xmlns.com/foaf/0.1" -install
Loading Model...
Publishing service on http://localhost:8080/kbox/query
Service up and running ;-) ...
```

### Querying a SPARQL endpoint

by console
```
java -jar kbox-v0.0.1-alpha3-RC16.jar -server "http://localhost:8080/kbox/query" -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}"
-------
| n   |
=======
| 123 |
-------
```
or JAVA API
```
URL url = new URL("Select (count(distinct ?s) as ?n) where {?s ?p ?o}");
ServerAddress serverURL = new ServerAddress("http://localhost:8080/kbox/query");
ResultSet rs = KBox.query(sparql, serverURL);
```

### Listing the resource folder

```
java -jar kbox-v0.0.1-alpha3-RC16.jar -r-dir
Your current resource directory is: kbox/dir/path
```

### Changing the resource folder

You might get problems with permissions using KBox in shared enviroments.
Therefore you can change the resource directory of KBox to your desired one.

```
java -jar kbox-v0.0.1-alpha3-RC16.jar -r-dir new/path
```

### Using KBox with Docker

You can also use KBox Docker container by following the steps below:

1) Install Docker in your machine (Consult the [guide](https://docs.docker.com/engine/installation/linux/ubuntu/) for more details).

```
sudo apt-get update
sudo apt-get -y install docker-engine
```

2) Pull KBox from AKSW hub repository.
```
docker pull aksw/kbox
```

3) Run it...
```
docker run aksw/kbox -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}" -kb "https://www.w3.org/2000/01/rdf-schema" -install
------
| n  |
======
| 32 |
------
```

4) Start a server using docker, but do not forget the --name flag:
```
docker run aksw/kbox --name myendpoint aksw/kbox -server -kb "https://www.w3.org/2000/01/rdf-schema" -install
Loading Model...
Publishing service on http://localhost:8080/kbox/query
Service up and running ;-) ...
```

5) You can also query your endpoint from a KBox docker container, but do not forget to use the --link flag:
```
docker run aksw/kbox --link myendpoint aksw/kbox -sparql "Select (count(distinct ?s) as ?n) where {?s ?p ?o}" -server "http://myendpoint:8080/kbox/query"
------
| n  |
======
| 32 |
------
```

### How can I publish my own Dataset?

Although you can create your own KNS service and publish your datasets, currently KBox does not allow you to directly publish content to make them available to other users.
In order to do that, you must contact us.

Contact: 
cbaron@informatik.uni-leipzig.de

Information needed:

1. Dataset Label;
2. Dataset Version;
3. The Publisher: Your or your organization's email/URL;
4. The Creator: Who has created the Knowledge base, e.g. DBpedia -> http://dbpedia.org;
5. The License: the dataset license;
6. The URL where the Knowledge graph file can be dereferenced (please create the file using KBox `-createIndex` command);
7. The Dataset URI name: the URI name that will be used by users to dereference your dataset;
8. The Dataset description: Give us a few words to help others to know what your dataset is about;
9. Tell us one reason why KBox is awesome. :-)
