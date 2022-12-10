[[_TOC_]]

# What is GIT used for?

In this course, unless otherwise explicitly stated, your exercises will be submitted via GitLab. The last commit pushed before the deadline to the master branch on GitLab is decisive for the evaluation, grading, and feedback. For the team assignment feedback will be provided on GitLab in a **DESIGN or FINAL** (team repository) milestone. To access these milestones check out the toolbar entry on the left named “Tickets” in German or “Issues” in English. The achieved points will be published using Moodle. The latter, i.e., Moodle will also cover your **Programming Tasks** feedback.

**Do not change the name of the master branch**: it must be called master. Only data that is in the master branch before the deadline (therefore committed and pushed before the deadline) will be taken into account during the submission interviews and evaluation. Double check that you use the correct repository (i.e., the one relevant for this semester and this course). 

# Why were two different repositories assigned to me?

We provide each student with two repositories. A **Team** repository and a **Private** repository. Check out the repository name to determine which one you are currently using. The team repository must be used by you and your team to submit and work on the assignment. In comparison, the private repository will only be available to you alone. Your team members will not be able to access it. You can use it, for example, to prepare drafts that you feel not yet ready to share with your team, to upload your programming task or worksheet progress, to receive tips and feedback from the tutors or lecturers on such uploads etc. For the latter aspect please start by contacting our tutors by creating a Git Issue.

The content upload (commited **&** pushed) to the team repository will be monitored and taken into account while grading. Uploads to the private repository will **only** be taking into account while grading with regards to the programming task uploads. 

# Is this repository the **Team** or the **Private** respository?

This repository is the **Team** repository. Use it to work on and submit your assignment submission. You and all your teammates can access this respository. 

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

# How are questions handled and how is support provided?

The `Tips and Recommendations` collection on Moodle gives an overview of different scenarios and recommended ways to contact us and get support. In the following, a short overview will be provided. In addition to the asynchronous methods listed below, synchronous QA slots are available:

- For **general inquiries** (which you deem relevant for **multiple teams**) please use the **Moodle forums**. In case of **individual questions** (which you deem relevant for **one person or team**), we recommend using **GitLab issues**. 

- Contact our **tutor first**. Especially for questions related to the implementation of the assignment or programming tasks. If our tutors cannot assist, they will forward your inquiry to the relevant supervisor. 

- To **contact our tutors**, create a GitLab Issue and add one of the following Git handles into your issue description text. The subsequently listed tutor(s) are available: 

    - `Samuel Mitterrutzner (Git handle @samuelm00)`

For **exceptional situations**, such as team-building issues, you can also contact your supervisor directly. The latter also applies to **high-level questions** on the assignment or the programming tasks. For example, if you see multiple design options and are unsure which one to use. Here, the experience of your supervisor can be tapped to guide you on your DSE journey. 

Your **supervisor** can be identified based on your team’s id. This semester the faculty needs more time to discuss internal teaching assignments. Thus we will provide you with this repository now such that you can get started early. Information on your supervisor is published later using Moodle.

**If nobody responds**: Based on our experience, most Git issues are not related to us and instead focus on internal team communication. Hence, if you want to contact a tutor or supervisor, **always use their Git handles in your issues description text**. Such that the respective person is notified about your inquiry by GitLab. To repeat: Do not assign a Git issue. We are only notified if you add the Git handle directly into the description text. **Do not use** @all as this would notify all supervisors, including those that are not relevant for you.

As a last resort, you can contact the course [email](mailto:dse@swa.univie.ac.at) or tutor [email](mailto:dse.tutor@swa.univie.ac.at). 

# Where will feedback and grades/points be published?

We will support you by publishing feedback (here, in this repository) and grades/points (on Moodle) on your team's semester project during the semester. The latter will use the DSE Moodle course grade book. The former uses GitLab's Milestone features. Use the left menu bar on the GitLab repository website to access your feedback. Choose: **Issues > Milestones**

# Which functions should not be used?

GitLab is a powerful software that allows you to customize numerous settings and choose from various features. We would advise you to treat these settings and features with **respect and care**. For example, by simply clicking on each button, ignoring warnings etc. one could delete this repositorie’s master branch for good (no we can’t restore it eather). So: Think before you click!

