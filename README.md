# FunCraft-IP-Generation

FunCraft recently closed, it used to be the biggest French Minecraft Server and brought some interesting ideas.

One of them is this IP Generation system, which was used for players using Offline accounts (No Mojang authentication) through their custom launcher. The whole idea of this system is to be able to recognize a player across multiple different accounts.

Using that system they could block users from joining using new accounts if they were banned on another one. Or detect hacked clients that join using an incorrectly generated IP, the whole system was heavily obfuscated in their launcher, with a lot of classes resembling the generation system but actually being baits.

A few projects have been made to generate the IP based on a username, but none of them were working correctly because they all missed the most important part of this system, which is the LauncherID, a key that is randomly generated once and then secretly stored in hidden files on the players computer, which could be compared to a HWID.

For more in depth explanation for the IP format you can take a look at [this repo](https://github.com/zyuiop/AZGenerator) which was the initial source of research for this project. The explanation is mostly correct except for the "24 random bytes" which is the LauncherID.

This repository is here for educational and software preservation purposes, it was used in Salwyrr Client to join the FunCraft server, now that FunCraft closed it won't cause them any issue.