package dev.morazzer.cookies.mod.repository.recipes.calculations;

import java.util.Stack;
import lombok.RequiredArgsConstructor;

/**
 * Context for the {@linkplain RecipeCalculator} to keep track of depth and blacklisted recipes.
 */
@RequiredArgsConstructor
public class CalculationContext {

    private final Stack<String> stack = new Stack<>();
    private final String[] blacklist;

    /**
     * Add an item to the recipe stack.
     *
     * @param item The item to add.
     */
    public void push(String item) {
        stack.push(item);
    }

    /**
     * Pops the uppermost stack element.
     *
     * @return The uppermost element.
     */
    public String pop() {
        return stack.pop();
    }

    /**
     * @param item The item.
     * @return Whether the item has already been visited.
     */
    public boolean hasBeenVisited(String item) {
        return stack.contains(item);
    }

    /**
     * @param item The item to check.
     * @return Whether the item can be used or not.
     */
    public boolean canVisit(String item) {
        for (String s : blacklist) {
            if (item.equals(s)) {
                return false;
            }
        }

        return true;
    }

}
