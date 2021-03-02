# What is GIT used for?

In this course, unless otherwise explicitly stated, your exercises will be submitted via GitLab. The last commit pushed before the deadline to the master branch on GitLab is decisive for the evaluation, grading, and feedback. The feedback will be provided here, i.e., on GitLab in a **DESIGN or FINAL milestone** respectively. To access these milestones check out the toolbar entry on the left named “Tickets” in German or “Issues” in English. The achieved points will be published using Moodle. 

**Do not change the name of the master branch**: it must be called master. Only data that is in the master branch before the deadline (therefore committed and pushed before the deadline) will be taken into account during the submission interviews and evaluation. Double check that you use the correct repository (i.e., the one relevant for this semester and this course). 

# Why are two different repositories available to me?

We provide each student with two repositories. A **Team** repository and a **Private** repository. Check out the repository name to determine which one you are currently using. The team repository must be used by you and your team to submit and work on the assignment. In comparison, the private repository will only be available to you alone. Your team members will not be able to access it. You can use it, for example, to prepare drafts that you feel not yet ready to share with your team, to upload your programming task or worksheet progress, to receive tips and feedback from the tutors or lecturers on such uploads etc. For the latter aspect please start by contacting our tutors by creating a Git Issue. Please use the following Git handle @millerk97 while doing so in your issue message. If our tutors are not able to address your inquiry we will be informed by them. 

The content upload (commited **&** pushed) to the team repository will be monitored and taken into account while grading. Uploads to the private repository will **not** be taken into account while grading. 

# How do I get local access to this repository?

To work optimally with this repository, you should mirror it to your local workstation. To do this, use the `git clone <yourRepoUrl>` command. To get hold of the required repository URL scroll up till you see a blue button labeled Clone - click it. Select the URL provided by “Clone with HTTPS”. This should be similar to https://git01lab.cs.univie.ac.at/......

An alternative would be “Clone with SSH”. We typically only recommend it if you have already a bit of experience with Git and SSH. For example, because this would require you to create public and private keys for authentication reasons.  

**Problems with the certificates**: If you are experiencing problems cloning your Git repository and you are experiencing problems with certificate validation, a quick solution is to turn it off (as a last resort). You can use the following command: git config --global http.sslVerify false

For team assignments the use of branches and branching based **source code management strategies** is recommended. The assignment provides tips and recommendations on this area. If you want to learn about branches in a relaxed tutorial environment we recommend to check out https://try.github.io/

# How do I use this repository?

Clone this repository as indicated above. Then you can interact with it based on standard git commands, such as, `git add`, `commit`, `push`, `checkout` etc. To do so you will need to specify your name and email address after the initial clone. This information is subsequently automatically used during each commit. Use the following commands to do so:

> `git config --global user.name "My name"`

> `git config --global user.email a123456@univie.ac.at`

Use your **real name** (i.e., not a nickname or an abbreviation) and your official **university mail address** (mandatory). Further, we recommend that you and your team members organize their efforts (e.g., work packages and their assignment) but also critical communication (e.g., if team members are unresponsive) here in GitLab using Git issues.

# How are questions handled?

For general inquiries (which are relevant for multiple teams) please use the Moodle forum. In case of individual inquiries please contact our **tutor first** - Miller Kevin (git handle @millerk97). If he is unable to provide assistance he will forward your inquiry to the relevant supervisor. 

For exceptional situations, such as, team building issues you can also contact your supervisor directly. Based on experience most Git issues are not related to us and instead focus on internal team communication. Hence, if you want to contact a tutor or supervisor, always use their Git handles in your issues such that the respective person is notified about your inquiry by GitLab.

Your **team supervisor** can be identified based on your team’s id. The supervisor for team `10X` is Georg Simhandl (git handle @georgs74). For teams `20X` the supervisor is Kristof Böhmer (git handle @boehmek2). Use these git handles to specifically address a person in a Git issue. 

# Which functions should not be used?

GitLab is a powerful software that allows you to customize numerous settings and choose from various features. We would advise you to treat these settings and features with **respect and care**. For example, by simply clicking on each button, ignoring warnings etc. one could delete this repositorie’s master branch for good (no we can’t restore it eather). So: Think before you click!

