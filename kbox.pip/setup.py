from setuptools import setup

with open("README.md", "r") as fh:
    long_description = fh.read()

setup(
    name="KBox",
    version="0.0.2-alpha",
    author="Edgard Marx",
    author_email='edgard.marx@htwk-leipzig.de',
    url='https://github.com/AKSW/KBox/tree/master/kbox.pip',
    description="Python distribution of the KBox.",
    long_description=long_description,
    long_description_content_type="text/markdown",
    py_modules=['kbox'],
    packages=["kbox"],
    package_data={'': ['*.jar']},
    include_package_data=True,
    install_requires=['click>=7.1.2'],
    entry_points={
        'console_scripts': [
            'kbox=kbox.kbox:execute_kbox_command',
        ],
    },
    license='Apache',
    classifiers=[
        "License :: OSI Approved :: Apache Software License",
        'Programming Language :: Python :: 3',
    ],

)
