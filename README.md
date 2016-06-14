# KBox


KBox is an abbreviation for Knowledge Box. 
The rationality behind KBox is to allow users to have a single place to share resources and knowledge among different applications as well as instances. 
Moreover, working on top of RDF model, KBox is a natural extension of the Web on your computer.

# Why use KBox?
Applications usually deal with resources and knowledge that are often duplicated amog several applications.
For instance, when using Starndford NLP library among different applications, the resources and knowledge inside the library is duplicated among different instances.
The idea is to have a common knwlewdge box where users can persist and yet retrieve and share without duplication.
In order to do that, we bring the RDF concept to KBox.
Thereafter, resource can be uniquely identified.

# What is possible to do with it?
With KBox you can share resources and knowledge among several applications, but not just that.
In other to permit the easier knowledge decimination, we have implemented Kibe library.
The Kibe library allows applications to virtually install and query RDF knowledge graphs.
It takes around ~10 minutes to start quering DBpedia on your computer, avoiding server overheads and faults.

# How can I use KBox?
You can use KBox in command line or the library on your application.
It is easy to plug and use it.

# How can I execute KBox in command Line?

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
* -kb-install  <kb-URL> - Install a given knowledgebase using the available KNS services to resolve it.
* -kb-install  <kb-URL> -index <indexFile> - Install a given index in a given knowledgebase URL.
* -kb-install  <kb-URL> -kns-server <kns-server-URL> - Install a knowledgebase from a a given KNS server.
* -kb-list  - List all available KNS services and knowledgebases.
* -r-dir <resourceDir> - Change the current path of the KBox resource container.
* -version - display KBox version.
```
