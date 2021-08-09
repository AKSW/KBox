import os
import subprocess

import click

DIR_PATH = os.path.dirname(os.path.realpath(__file__))
JAR_EXECUTE = "java -jar " + DIR_PATH + "/kbox-v0.0.2-alpha.jar"  # kbox-v0.0.2-alpha.jar
SPACE = " "
PUSH_COMMAND = 'push'
COMMAND_LIST = [PUSH_COMMAND]


@click.command(context_settings={"ignore_unknown_options": True})
@click.argument('commands', nargs=-1)
def execute_kbox_command(commands):
    args = SPACE
    for item in commands:
        args += item + SPACE
    execute_commands = JAR_EXECUTE + args
    try:
        process = subprocess.Popen(execute_commands.split(), stdout=subprocess.PIPE)
        output, err = process.communicate()
        output = output.decode("utf-8")
        click.echo(output)
    except OSError as e:
        click.echo(e)


def execute(commands):
    commands = tuple(commands.split())
    args = SPACE
    for item in commands:
        args += item + SPACE
    execute_commands = JAR_EXECUTE + args
    try:
        process = subprocess.Popen(execute_commands.split(), stdout=subprocess.PIPE)
        output, err = process.communicate()
        output = output.decode("utf-8")
        return output
    except OSError as e:
        print(e)
