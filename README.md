# Ola Play
Submitted for HackerEarth's Ola Play Challenge

This app is (currently) a music player that fetches a list of songs from a URL (provided by the challenge creators) and the user can play / download / like them.
 
It uses Android's Room Persistence Library (my first go around with it). I wasn't sure exactly how the transactions are to be handled in the background so I created AsyncTasks to take care of that. I am not sure whether Room provides background transactions on its own. It's possible that that's not the case (hence the 'allowMainThreadQueries' parameter), though I could be wrong.

There may be more info here later.
