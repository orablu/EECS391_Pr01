/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
//package edu.cwru.sepia.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

/**
 * This agent will first collect gold to produce a peasant, then the two
 * peasants will collect gold and wood separately until reach goal.
 *
 * @author Feng
 *
 */
public class SearchAgent extends Agent {
    private static final long serialVersionUID = -4047208702628325380L;
    private static final Logger logger = Logger.getLogger(SearchAgent.class
            .getCanonicalName());

    private int step;

    public SearchAgent(int playernum, String[] arguments) {
        super(playernum);

    }

    StateView currentState;

    private List<GraphNode> getPathToTownHall(StateView currentState,
            UnitView footman, UnitView townhall) {

        // Create the map, leaving occupied cells blank.
        GraphNode nodeMap[][] = new GraphNode[currentState.getXExtent()][currentState
                .getYExtent()];
        for (int i = 0; i < currentState.getXExtent(); i++) {
            for (int j = 0; j < currentState.getYExtent(); j++) {
                if (!currentState.isUnitAt(i, j)
                        && !currentState.isResourceAt(i, j)) {
                    nodeMap[i][j] = new GraphNode(i, j);
                }
            }
        }

        // Get the nodes containing the our target and origin.
        GraphNode initial = new GraphNode(footman.getXPosition(),
                footman.getYPosition());
        GraphNode target = new GraphNode(townhall.getXPosition(),
                townhall.getYPosition());
        return getPathToTarget(nodeMap, initial, target);
    }

    private List<GraphNode> getPathToTarget(GraphNode map[][],
            GraphNode initial, GraphNode target) {
        WeightedNode current = new WeightedNode(initial, null);

        // Search for the town hall.
        List<WeightedNode> openSet = new ArrayList<>();
        List<GraphNode> closedSet = new ArrayList<>();

        while (true) {
            openSet.remove(current);
            List<GraphNode> adjacent = getAdjacentNodes(map, current.getNode(),
                    closedSet);

            // Find the adjacent node with the lowest heuristic cost.
            for (GraphNode node : adjacent) {
                openSet.add(new WeightedNode(node, current));
            }

            // Exit search if done.
            if (openSet.isEmpty() || targetAdjacent())
                break;

            // This node has been explred now.
            closedSet.add(current.getNode());

            // Find the next open node with the lowest cost.
            WeightedNode next = openSet.get(0);
            for (WeightedNode node : openSet) {
                if (node.getCost() < next.getCost())
                    next = node;
            }
            current = next;
        }

        // Collect the path.
        List<GraphNode> path = new ArrayList<>();
        while (current.getParent() != null) {
            path.add(0, current.getNode());
            current = current.getParent();
        }

        return path;
    }

    private boolean targetAdjacent(GraphNode current, GraphNode target) {
        for (int i = current.x - 1; i < current.x + 1; i++) {
            for (int j = current.y - 1; j < current.y + 1; j++) {
                if (i == target.x && j == target.y)
                    return true;
            }
        }
        return false;
    }

    private List<GraphNode> getAdjacentNodes(GraphNode map[][],
            GraphNode current, List<GraphNode> visited) {
        int x = current.x;
        int y = current.y;
        List<GraphNode> nodes = new ArrayList<>();
        for (int i = Math.max(x - 1, 0); i < Math.min(x + 2, map.length); i++) {
            for (int j = Math.max(y - 1, 0); j < Math.min(y + 2, map[i].length); j++) {
                if ((i != x && j != y) && map[i][j] == null
                        && !visited.contains(map[i][j])) {
                    nodes.add(map[i][j]);
                }
            }
        }
        return nodes;
    }

    @Override
    public Map<Integer, Action> initialStep(StateView newstate,
            History.HistoryView statehistory) {
        step = 0;

        List<Integer> unitIds = currentState.getUnitIds(playernum);
        List<Integer> townhallIds = new ArrayList<Integer>();
        List<Integer> footmanIds = new ArrayList<Integer>();
        for (int i = 0; i < unitIds.size(); i++) {
            int id = unitIds.get(i);
            UnitView unit = currentState.getUnit(id);
            String unitTypeName = unit.getTemplateView().getName();
            System.out.println("Unit Type Name: " + unitTypeName);
            if (unitTypeName.equals("TownHall"))
                townhallIds.add(id);
            if (unitTypeName.equals("Footman"))
                footmanIds.add(id);
        }

        UnitView footman = currentState.getUnit(footmanIds.get(0));
        UnitView townhall = currentState.getUnit(townhallIds.get(0));

        List<GraphNode> path = getPathToTownHall(currentState, footman,
                townhall);

        return middleStep(newstate, statehistory);
    }

    @Override
    public Map<Integer, Action> middleStep(StateView newState,
            History.HistoryView statehistory) {
        step++;

        Map<Integer, Action> builder = new HashMap<Integer, Action>();
        currentState = newState;

        return builder;
    }

    @Override
    public void terminalStep(StateView newstate,
            History.HistoryView statehistory) {
        step++;
    }

    public static String getUsage() {
        return "Naviagtes a footman to an enemy town hall";
    }

    @Override
    public void savePlayerData(OutputStream os) {
        // this agent lacks learning and so has nothing to persist.
    }

    @Override
    public void loadPlayerData(InputStream is) {
        // this agent lacks learning and so has nothing to persist.
    }

    /**
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(int step) {
        this.step = step;
    }
}
