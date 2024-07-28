/**
 *
 */
package com.omo.free.simple.fx.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.omo.free.util.AppUtil;

/**
 * The Credit class is for holding names of people who are to receive credit for the work they have done.
 *
 * <p>Note that the {@code Credit} class is used when calling the {@link SFXViewBuilder#addStandardMenuBar(Credit)} method</p>
 *
 * <p>The types of work that this {@code Credit} class encapsulates are:</p>
 *
 * <ul>
 *  <li>Project Manager(s)</li>
 *  <li>Lead Developer(s)</li>
 *  <li>Developer(s)</li>
 *  <li>Contributor(s)</li>
 *  <li>Documentation</li>
 *  <li>Graphics</li>
 *  <li>SimpleFX Framework Author(s)</li>
 * </ul>
 *
 * <p><b>Example</b></p>
 * <p>The following shows an example instantiation of this class.</p>
 * <pre><code>
 *      .
 *      .
 *      .
 *      Credit credit = new Credit();
 *      credit.addProjectManager("Dwayne Walker");
 *      credit.addLeadDeveloper("Richard Salas");
 *      credit.addDevelopers("Brian Hicks");
 *      credit.addContributor("Don Brown");
 *      credit.addDocumentors("Johnny Stidum");
 *      <b>addStandardMenuBar(credit);</b>
 *      .
 *      .
 *      .
 * </code></pre>
 *
 * @author Richard Salas JCCC
 * @author modified by Ron Skinner JCCC 01/05/2021
 * @version 1.1
 * @see SFXViewBuilder#addStandardMenuBar(Credit)
 */
public class Credit {

    public static final String SIMPLE_FX_AUTHOR = "Richard Salas";
    public static final String SIMPLE_FX_GRAPHIC_AUTHOR = "Jim Cox";

    public List<String> projectManagers;
    public List<String> leadDevelopers;
    public List<String> developers;
    public List<String> contributors;
    public List<String> documenters;

    /**
     * Creates an instance of the {@code Credit} class which will initialize the lists within this class.
     */
    public Credit() {
        projectManagers = new ArrayList<>();
        leadDevelopers = new ArrayList<>();
        contributors = new ArrayList<>();
        documenters = new ArrayList<>();
        developers = new ArrayList<>();
    }//end credit

    /**
     * This method will add a project manager to a list.
     *
     * @param name the project manager to add to the list
     */
    public void addProjectManager(String name) {
        if(AppUtil.isNullOrEmpty(name)){
            return;
        }//end if
        projectManagers.add(name);
    }//end method

    /**
     * This method will add a list of names to the project managers list.
     * @param names list of names to add to the project manager lists
     */
    public void addProjectManagers(List<String> names) {
        if(AppUtil.isEmpty(names)){
            return;
        }//end if
        projectManagers.addAll(names);
    }//end method

    /**
     * This method will add an array of names to the project managers list.
     * @param names array of names to add to the project manager lists
     */
    public void addProjectManagers(String... names) {
        if(AppUtil.isEmptyArray(names)){
            return;
        }//end if
        projectManagers.addAll(Arrays.asList(names));
    }//end method

    /**
     * This method will get the List&lt;String&gt; of Project Managers.
     * 
     * @return List&lt;String&gt; of projectManagers
     */
    public List<String> getProjectManagers() {
        return projectManagers;
    }//end method

    /**
     * This method will check the List&lt;String&gt; of project managers to see if it contains the person's name.
     * 
     * @param name
     *        the name of the person involved
     * @return true if the person is a project manager
     */
    public boolean isAProjectManager(String name) {
        return projectManagers.contains(name);
    }// end method

    /**
     * This method will add a lead developer name to the lead developer list.
     *
     * @param name
     *        the lead developer to add to the list
     */
    public void addLeadDeveloper(String name) {
        if(AppUtil.isNullOrEmpty(name)){
            return;
        }//end if
        leadDevelopers.add(name);
    }//end method

    /**
     * This method will add a list of names to the lead developers list.
     * @param names list of names to add to the lead developers lists
     */
    public void addLeadDevelopers(List<String> names) {
        if(AppUtil.isEmpty(names)){
            return;
        }//end if
        leadDevelopers.addAll(names);
    }//end method

    /**
     * This method will add an array of names to the lead developers list.
     * @param names array of names to add to the lead developers lists
     */
    public void addLeadDevelopers(String... names) {
        if(AppUtil.isEmptyArray(names)){
            return;
        }//end if
        leadDevelopers.addAll(Arrays.asList(names));
    }//end method

    /**
     * This method will get the List&lt;String&gt; of Lead Developers.
     * 
     * @return List&lt;String&gt; of leadDevelopers
     */
    public List<String> getLeadDevelopers() {
        return leadDevelopers;
    }//end method

    /**
     * This method will check the List&lt;String&gt; of lead developers to see if it contains the person's name.
     * 
     * @param name
     *        the name of the person involved
     * @return true if the person is a lead developer
     */
    public boolean isALeadDeveloper(String name) {
        return leadDevelopers.contains(name);
    }// end method

    /**
     * This method will add a developer name to the developer list.
     *
     * @param name
     *        the developer to add to the list
     */
    public void addDeveloper(String name) {
        if(AppUtil.isNullOrEmpty(name)){
            return;
        }// end if
        developers.add(name);
    }// end method

    /**
     * This method will add an array of names to the developer list.
     * 
     * @param names
     *        array of names to add to the developer lists
     */
    public void addDevelopers(List<String> names) {
        if(AppUtil.isEmpty(names)){
            return;
        }// end if
        developers.addAll(names);
    }// end method

    /**
     * This method will add an array of names to the developer list.
     * 
     * @param names
     *        array of names to add to the developer lists
     */
    public void addDevelopers(String... names) {
        if(AppUtil.isEmptyArray(names)){
            return;
        }// end if
        developers.addAll(Arrays.asList(names));
    }// end method

    /**
     * This method will get the List&lt;String&gt; of Developers.
     * 
     * @return List&lt;String&gt; of developers
     */
    public List<String> getDevelopers() {
        return developers;
    }// end method

    /**
     * This method will check the List&lt;String&gt; of developers to see if it contains the person's name.
     * 
     * @param name
     *        the name of the person involved
     * @return true if the person is a developer
     */
    public boolean isADeveloper(String name) {
        return developers.contains(name);
    }// end method

    /**
     * This method will add a documenter name to the documenters list.
     *
     * @param name
     *        the documenter to add to the list
     */
    public void addDocumentor(String name) {
        if(AppUtil.isNullOrEmpty(name)){
            return;
        }// end if
        documenters.add(name);
    }// end method

    /**
     * This method will add a list of names to the documenters list.
     * 
     * @param names
     *        list of names to add to the documenters lists
     */
    public void addDocumentors(List<String> names) {
        if(AppUtil.isEmpty(names)){
            return;
        }// end if
        documenters.addAll(names);
    }// end method

    /**
     * This method will add an array of names to the documenters list.
     * 
     * @param names
     *        array of names to add to the documenters lists
     */
    public void addDocumentors(String... names) {
        if(AppUtil.isEmptyArray(names)){
            return;
        }// end if
        documenters.addAll(Arrays.asList(names));
    }// end method

    /**
     * This method will get the List&lt;String&gt; of documenters.
     * 
     * @return List&lt;String&gt; of documenters
     */
    public List<String> getDocumentors() {
        return documenters;
    }// end method

    /**
     * This method will check the List&lt;String&gt; of documenters to see if it contains the person's name.
     * 
     * @param name
     *        the name of the person involved
     * @return true if the person is a documenter
     */
    public boolean isADocumentor(String name) {
        return documenters.contains(name);
    }// end method

    /**
     * This method will add a contributor name to the contributors list.
     *
     * @param name the contributor to add to the list
     */
    public void addContributor(String name) {
        if(AppUtil.isNullOrEmpty(name)){
            return;
        }//end if
        contributors.add(name);
    }//end method

    /**
     * This method will add a list of names to the contributors list.
     * @param names list of names to add to the contributors lists
     */
    public void addContributors(List<String> names) {
        if(AppUtil.isEmpty(names)){
            return;
        }//end if
        contributors.addAll(names);
    }//end method

    /**
     * This method will add an array of names to the contributors list.
     * @param names array of names to add to the contributors lists
     */
    public void addContributors(String... names) {
        if(AppUtil.isEmptyArray(names)){
            return;
        }//end if
        contributors.addAll(Arrays.asList(names));
    }//end method

    /**
     * This method will get the List&lt;String&gt; of Contributors.
     * 
     * @return List&lt;String&gt; of contributors
     */
    public List<String> getContributors() {
        return contributors;
    }//end method

    /**
     * This method will check the List&lt;String&gt; of contributors to see if it contains the person's name.
     * 
     * @param name
     *        the name of the person involved
     * @return true if the person is a contributors
     */
    public boolean isAContributor(String name) {
        return contributors.contains(name);
    }// end method

    /**
     * @return the SimpleFX Framework author list
     */
    public List<String> getSimpleFXAuthors() {
        return Arrays.asList(SIMPLE_FX_AUTHOR);
    }// end method

    /**
     * @return the graphics artists list
     */
    public List<String> getGraphicArtists() {
        return Arrays.asList(SIMPLE_FX_GRAPHIC_AUTHOR);
    }// end method

    /**
     * Returns a string representation of the {@code Credit} class.
     *
     * @return  a string representation of the object.
     */
    @Override public String toString() {
        final int maxLen = 10;
        StringBuilder builder = new StringBuilder();
        builder.append("Credit [projectManagers=");
        builder.append(projectManagers != null ? projectManagers.subList(0, Math.min(projectManagers.size(), maxLen)) : null);
        builder.append(", leadDevelopers=");
        builder.append(leadDevelopers != null ? leadDevelopers.subList(0, Math.min(leadDevelopers.size(), maxLen)) : null);
        builder.append(", developers=");
        builder.append(developers != null ? developers.subList(0, Math.min(developers.size(), maxLen)) : null);
        builder.append(", contributors=");
        builder.append(contributors != null ? contributors.subList(0, Math.min(contributors.size(), maxLen)) : null);
        builder.append(", documentators=");
        builder.append(documenters != null ? documenters.subList(0, Math.min(documenters.size(), maxLen)) : null);
        builder.append("]");
        return builder.toString();
    }//end method

}//end class
