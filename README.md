AMT-GEOGAME
===========

1) Run the command "ant amtsite" to create a task.

2) After the compilation,you should see a HIT id and a URL. Make note of the HIT id, you will need it 
   for reviewing the task.

3) Use the URL to access the task (game).

4) For reviewing the task run the following command
	ant -DhitID=<<HIT id of the task you want to review>> reviewerForHIT

	For eg:
	ant -DhitID=2BVD8XMB3Q39D6H6KLF6W4EVXFFGRL reviewerForHIT

