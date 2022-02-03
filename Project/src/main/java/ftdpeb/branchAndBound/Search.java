package ftdpeb.branchAndBound;

import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.trees.TreesManager;

public class Search {
	
	public Solution PerformSearch(Route originalRoute) {
		Solution sol = new Solution();


		TreesManager treeManager = new TreesManager();
		sol = treeManager.NextTree(originalRoute, null, null, -1, -1, null, -1, false);
		
		
		return sol; 
		
	}

}
