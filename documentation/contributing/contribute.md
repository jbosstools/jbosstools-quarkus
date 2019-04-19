<!--
  ~ Copyright 2019 Red Hat, Inc.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

# Contribution guide

**Want to contribute? Great!** 
We try to make it easy, and all contributions, even the smaller ones, are more than welcome.
This includes bug reports, fixes, documentation, examples... 
But first, read this page (including the small print at the end).

## Legal

All original contributions to Quarkus are licensed under the
[ASL - Apache License](https://www.apache.org/licenses/LICENSE-2.0),
version 2.0 or later, or, if another license is specified as governing the file or directory being
modified, such other license.

All contributions are subject to the [Developer Certificate of Origin (DCO)](https://developercertificate.org/).
The DCO text is also included verbatim in the [dco.txt](../../dco.txt) file in the root directory of the repository.

## Reporting an issue

This project uses GitHub issues to manage the issues. Open an issue directly in GitHub.

If you believe you found a bug, and it's likely possible, please indicate a way to reproduce it, what you are seeing and what you would expect to see.
Don't forget to indicate your Java and Eclipse version. 

## Before you contribute

To contribute, use GitHub Pull Requests, from your **own** fork.

### Import the Projects

The different projects constituting the Quarkus Eclipse tooling can be imported in your Eclipse workspace by using the Eclipse import wizard. Bring up the wizard by selecting `File->Import...`

<img src="images/import-wizard.png" width="350"/>

Select the `Maven->Existing Maven Projects` wizard and push the `Next` button.

<img src="images/select-root.png" width="350"/>

Push the `Browse...` button and navigate to the root of your quarkus-eclipse clone. Perform the import by pushing the `Finish` button.

### Work on a Topic Branch

When you want to contribute code or documentation you first need to create a topic branch. It is common practice to give your branch a name that refers to the issue you are solving. E.g. if you want to fix the issue #2, name your topic branch 'QE-2' (`git checkout -b QE-2`). 

When the work is done or the issue is (partly) fixed, squash the commits into one. It is good practice to refer to the solved issue in the commit message. E.g. `git commit -m "QE-2: Create contribution guide - Add a 'Work on a Topic Branch' section"`). 

Rebase your topic branch against the current master branch if necessary (`git rebase master`), push the branch to your own fork on GitHub (`git push <your-fork> QE-2`) and open a pull request (`https://github.com/<your-fork>/quarkus-eclipse/pull/new/QE-2`).

## The small print

This project is an open source project, please act responsibly, be nice, polite and enjoy!
