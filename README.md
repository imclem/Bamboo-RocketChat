This is a bamboo plugin for sending notifications to RocketChat Channel.

Just a reminder of the License, this is provided "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND".

# Installation
1. Download the jar from this github project [Release](https://github.com/imclem/Bamboo-RocketChat/releases) Tab
2. Go to your **Manage add-ons** tab in Bamboo administration
3. Click on **Upload add-on** and pick-up the jar you previously downloaded

# Configure
1. Go to your **RocketChat Server** tab in Bamboo administration
2. Fill all the fields, be careful the Server url should end with **/api/v1**

# Use it
You can now use it from the **Notifications** tab in your plan configuration.

# Known limitations
At this time, you can't use LDAP accounts and must use RocketChat local accounts.

# Working Versions
- Bamboo v6.0.0
- It might work with other versions and I'll really appreciate that you tell me if it works with your version

# Development
1. Download and install the Atlassian Plugin SDK.
2. Run **atlas-package** from the project root.
3. Grab your jar file that has just been generated in the newly created **target** directory.

# Thanks
I would like to specially thanks guys from [Sofico](http://www.sofico.be) for sharing their code with the world. Original code can be found here [BitBucket](https://bitbucket.org/sofico/bamboo-sametime-plugin).

This work is a hundred percent derivated from Sofico's code.
