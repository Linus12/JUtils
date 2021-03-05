# JUtils
A collection of common classes I use in most of my larger projects to do the same things I do over and over again.

Currently there are 13 classes covering 10 different areas:
<ul><li>Args for handling program arguments.
<li>Dates for handling Dates.
<li>DebugInfo/DebugStatus for implementing a quick and dirty Debug processing. In conjunction with Trace and TraceThread it allows the program to print debugging messages by simply flipping a flag, rather than constant "if/then" logic all over the code.
<li>Errs to pass error types (None, Warnings, Errors), Error number, Error message, Error field, and Stacktraces between classes. Allows calling classes to ignore errors if so desired.
<li>FileSearch and FIO to handle most of the basic File IO operations
<li>GuiInfo is a simple class to handle basic 'windowed GUI' parameters.
<li>Help allows for the reading and definition of multiple help files for your project.
<li>Sleep is a collection of routines for "sleeping" the program/thread
<li>Trace and TraceThread allow the printing, displaying, and formatting of messages, warnings, errors, etc. on the output, desired, whether that is the console, windowed areas, or external files. This makes the code much cleaner because you just code "printMsg" and the class determines where it is to be displayed.
<li>WindowRpt is a simple swing class that opens a "window" that can be written on to display text like "Help", or Trace messages.
</ul>
All of these classes were written originally as part of a project and embedded in it. As subsequent projects were written I pulled them out of the original project and placed them here in my "toolbox". They are not the most elegant of implementations, and I'm sure they could be improved by those more proficient in Java. But I feel the code is pretty well documented so someone else can use the ideas to see where I was going with them and how they might use them, or their derivatives, in their own projects.