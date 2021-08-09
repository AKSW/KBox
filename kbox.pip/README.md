# KBox
KBox python implementation

This package is created to distribute KBox as a python package.

* View the library from [here](https://pypi.org/project/kbox/)

* Install using pip
```
pip install kbox
```

### Use it in from your terminal
Once you install the kbox package, you can directly execute the commands from the terminal. You don't need to open 
up a python environment to use the kbox package.

Open a terminal and execute KBox commands in python with kbox package as below,

````
kbox -list
````

````
KBox KNS Resource table list
##############################
name,format,version
##############################
https://schema.org/,kibe,0
http://productcatalogue.org/products/eco/,kibe,0
http://productcatalogue.org/products/amazon/,kibe,0
http://company.org,kibe,0
http://github.org/aksw/NSpM/monument_300_2,kibe,0
http://github.org/aksw/NSpM/monument_300,NSPM/Model,0
http://purl.org/dc/dcmitype/,kibe,0
http://purl.org/dc/dc0/,kibe,0
...
...

````

Like the above command, you can use all other KBox commands with kbox python package. You can refer to the document 
[here](https://github.com/AKSW/KBox#how-can-i-execute-kbox-in-command-line) to get a good understanding of other KBox commands as well. 

### Use it in your python application.

##### execute(command)
    Description: Execute the provided command in the KBox.jar
    Args:
      command: 'string', KBox command which should be exectue in KBox.
    Returns:
        Resutls of the provided kbox command as a string

If you want to use the kbox inside your python application, you can follow these instructions,
1. Import the kbox package (`from kbox import kbox`).
2. Execute any KBox command with execute() function as follows.
   
   ```
   kbox.execute('KBox_Command')
   ```

**Note: `execute()` method will return a string output which contains the result of the executed command.
    
### Source URLs
* Find the KBox source code [here](https://github.com/AKSW/KBox)
