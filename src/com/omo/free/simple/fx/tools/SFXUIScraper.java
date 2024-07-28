package com.omo.free.simple.fx.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * SFXUIScraper interface is a {@link FunctionalInterface} that enables the user the ability to return a {@code List<Node>} which are located inside the root {@code Pane}. The user must implement this interface's {@link #getRoot() getRoot} method to return the top-level {@code Parent} where the user wants the recursive search to begin.
 * </p>
 * <b>Example</b>
 * <p>
 * The following example will show you a simple implementation of the {@code SFXUIScraper} interface. This example illustrates the use of the {@link #scrape(Predicate) scrape} method which allows the developer to pass a lambda expression that will be used to filter the return results.
 * </p>
 *
 * <pre>
 * <code>
public class ApplicationView extends SFXViewBuilder implements SFXUIScraper {

    VBox root;

    {@literal @Override}
    protected Parent buildParent() {
        root = new VBox();
        Label label = new Label("Label");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField textField = new TextField("Test Text");
        Button button = new Button("Enter");

        datePicker.setId("datePickerId");
        textField.setId("textFieldId");
        button.setId("buttonId");

        button.setOnAction(e -> {
            SFXUIScraper scraper = () -> root;
            {@literal List<Node>} nodes = scraper.scrape(n -> null != n.getId() && n instanceof Control);
            for(int i = 0, j = nodes.size(); i < j; i++){
                Node node = nodes.get(i);
                System.out.println(node.getId());
            }
        });

        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(5));
        root.getChildren().addAll(label, datePicker, textField, button);

        Credit credit = new Credit();
        credit.addLeadDeveloper("Brandon Turner");
        addStandardMenuBar(credit);

        return root;
    }// end buildParent

    {@literal @Override} 
    public Parent getRoot() {
        return root;
    }// end getRoot
}//end class
 * </code>
 * </pre>
 * <p>
 * In the example, when the user clicks the {@code Button}, the SFXUIScraper is instantiated using the "root" class as a parameter. The {@link #scrape(Predicate) scrape} method is then called and it is passed a lambda expression that filters the returned list two ways:
 * <ol>
 * <li>The {@code Node} must not have an id value of {@code null}.</li>
 * <li>The {@code Node} must be an instance of the {@code Control} class.</li>
 * </ol>
 * Then the results are iterated through and each of the Nodes id's are printed to the console.
 * </p>
 * <p>
 * <b>Developer's Note:</b> The {@code FunctionalInterface} is not instantiated like standard interfaces. Instead, they are instantiated as a lambda expression. The benefit of using a {@code FunctionalInterface} is that the user can pass a section of code that would normally not be used elsewhere in the application. As such, the user can instantiate the same {@code FunctionalInterface} using different lambda expressions, dependent only on what functionality is needed at the time of creation.
 * </p>
 * <p>
 * The lambda expression that is passed to the {@link #scrape(Predicate) scrape} method can be modified to filter as general or as specific as needed. The above is just an example of a possible use. Below are other examples that could be used:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Expression</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>scraper.scrape(n -> n instanceof TextField);</td>
 * <td>Gets all Nodes that are TextFields</td>
 * </tr>
 * <tr>
 * <td>scraper.scrape(n -> n instanceof TextField && !((TextField) n).getText().equals(""));</td>
 * <td>Gets all Nodes that are TextFields AND are not empty.</td>
 * </tr>
 * <tr>
 * <td>scraper.scrape(n -> n instanceof Text || n instanceof Label);</td>
 * <td>Gets all Nodes that are Text OR Label controls.</td>
 * </tr>
 * <tr>
 * <td>scraper.scrape(n -> null != n.getId());</td>
 * <td>Gets all Nodes that have an Id value set.</td>
 * </tr>
 * <caption>Lambda Expression Examples</caption>
 * </table>
 * </p>
 * 
 * @author <strong>Brandon Turner</strong> JCCC - ITSD Dec 01, 2023
 */
@FunctionalInterface
public interface SFXUIScraper {

    /**
     * Implementation of this method must return a {@code Parent}.
     * </p>
     * <b>Example</b>
     * <p>
     * <i>Assuming your Parent is called root:</i><br>
     * <b>return root;</b>
     * </p>
     * <p>
     * <i>The following is also an option:</i><br>
     * <b>return SFXViewBuilder.getPrimaryStage().getScene().getRoot();</b>
     * </p>
     * 
     * @return The desired top-level {@code Parent}.
     */
    Parent getRoot();

    /**
     * Default implementation used to dynamically and recursively find all Nodes of the specified class.
     * 
     * @param filter
     *        The lambda expression used to filter the results.
     * @return {@code List<Node>}
     */
    default List<Node> scrape(Predicate<Node> filter) {
        Parent top = getRoot();
        List<Node> result = new ArrayList<>();
        scrape(filter, result, top);
        return result;
    }// end scrape

    /**
     * Recursively finds all Nodes of the specified class, starting at the parent.
     * 
     * @param filter
     *        The lambda expression used to filter the results.
     * @param result
     *        The {@code ArrayList<Node>} to add the retrieved Nodes to.
     * @param top
     *        The Parent level to start the recursive search at.
     */
    static void scrape(Predicate<Node> filter, List<Node> result, Parent top) {
        ObservableList<Node> childrenUnmodifiable = top.getChildrenUnmodifiable();
        Node node = null;
        for(int i = 0, j = childrenUnmodifiable.size();i < j;i++){
            node = childrenUnmodifiable.get(i);
            if(null != node){
                if(filter.test(node)){
                    result.add(node);
                }// end if
                if(node instanceof Parent){
                    scrape(filter, result, (Parent) node);
                }// end if
            }// end if
        }// end for
    }// end scrape

}// end interface
