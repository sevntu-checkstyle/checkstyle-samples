////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright(C) 2001-2012  Oliver Burn
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check verifies the name of JUnit4 test class for compliance with user
 * defined naming convention(by default Check expects test classes names
 * matching
 * ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*"
 * regex).
 * </p>
 * <p>
 * Class is considered to be a test if its definition or one of its method
 * definitions annotated with user defined annotations. By default Check looks
 * for classes which contain methods annotated with "Test" or "org.junit.Test".
 * </p>
 * <p>
 * Check has following options:
 * </p>
 * <p>
 * "expectedClassNameRegex" - regular expression which matches expected test
 * class names. If test class name does not matches this regex then Check will
 * log violation. This option defaults to
 * ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*".
 * </p>
 * <p>
 * "classAnnotationNameRegex" - regular expression which matches test annotation
 * names on classes. If class annotated with matching annotation, it is
 * considered to be a test. This option defaults to empty regex(one that matches
 * nothing). If for example this option set to "RunWith", then class "SomeClass"
 * is considered to be a test:
 * 
 * <pre>
 * <code>
 * {@literal @}RunWith(Parameterized.class)
 * class SomeClass
 * {
 * }
 * </code>
 * </pre>
 * 
 * </p>
 * <p>
 * "methodAnnotationNameRegex" - regular expression which matches test
 * annotation names on methods. If class contains a method annotated with
 * matching annotation, it is considered to be a test. This option defaults to
 * "Test|org.junit.Test". For example, if this option set to "Test", then class
 * "SomeClass" is considered to be a test.
 * 
 * <pre>
 * <code>
 * class SomeClass
 * {
 *      {@literal @}Test
 *      void method() {
 *      
 *      }
 * }
 * </code>
 * </pre>
 * 
 * </p>
 * <p>
 * Annotation names must be specified exactly the same way it specified in code,
 * thus if Check must match annotation with fully qualified name, corresponding
 * options must contain qualified annotation name and vice versa. For example,
 * if annotation regex is "org.junit.Test" Check will recognize "{@literal @}
 * org.junit.Test" annotation and will skip "{@literal @}Test" annotation and
 * vice versa if annotation regex is "Test" Check will recognize "{@literal @}
 * Test" annotation and skip "{@literal @}org.junit.Test" annotation.
 * </p>
 * <p>
 * Following configuration will adjust Check to look for classes annotated with
 * annotation "RunWith" or classes with methods annotated with "Test" and verify
 * that classes names end with "Test" or "Tests".
 * 
 * <pre>
 *     &ltmodule name="NameConventionForJUnit4TestClassesCheck"&gt
 *       &ltproperty name="expectedClassNameRegex" value=".+Tests|.+Test"/&gt
 *       &ltproperty name="classAnnotationNameRegex" value="RunWith"/&gt
 *       &ltproperty name="methodAnnotationNameRegex" value="Test"/&gt
 *     &lt/module&gt
 * </pre>
 * </p>
 * 
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 */
public class NameConventionForJunit4TestClassesCheck extends Check
{
    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "name.convention.for.test.classes";

    /**
     * <p>
     * Regular expression which matches expected names of JUnit test classes.
     * </p>
     * <p>
     * Default value is
     * ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*".
     * </p>
     */
    private Pattern mExpectedClassNameRegex =
            Pattern.compile(".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*");

    /**
     * <p>
     * Regular expression which matches JUnit class test annotation names.
     * </p>
     * <p>
     * By default this regex is empty.
     * </p>
     */
    private Pattern mClassAnnotationNameRegex;

    /**
     * <p>
     * Regular expression which matches JUnit method test annotation names.
     * </p>
     * <p>
     * Default value is "Test|org.junit.Test".
     * </p>
     */
    private Pattern mMethodAnnotationNameRegex =
            Pattern.compile("Test|org.junit.Test");

    /**
     * Sets regexp to match 'expected' class names for JUnit tests.
     * @param aExpectedClassNameRegex
     *        regexp to match 'correct' JUnit test class names.
     */
    public void setExpectedClassNameRegex(String aExpectedClassNameRegex)
    {
        if (aExpectedClassNameRegex != null && !aExpectedClassNameRegex.isEmpty()) {
            mExpectedClassNameRegex = Pattern.compile(aExpectedClassNameRegex);
        }
        else {
            mExpectedClassNameRegex = null;
        }
    }

    /**
     * Sets class test annotation name regexp for JUnit tests.
     * @param aAnnotationNameRegex
     *        regexp to match annotations for unit test classes.
     */
    public void setClassAnnotationNameRegex(String aAnnotationNameRegex)
    {
        if (aAnnotationNameRegex != null && !aAnnotationNameRegex.isEmpty()) {
            mClassAnnotationNameRegex = Pattern.compile(aAnnotationNameRegex);
        }
        else {
            mClassAnnotationNameRegex = null;
        }
    }

    /**
     * Sets method test annotation name regexp for JUnit tests.
     * @param aAnnotationNameRegex
     *        regexp to match annotations for unit test classes.
     */
    public void setMethodAnnotationNameRegex(String aAnnotationNameRegex)
    {
        if (aAnnotationNameRegex != null && !aAnnotationNameRegex.isEmpty()) {
            mMethodAnnotationNameRegex = Pattern.compile(aAnnotationNameRegex);
        }
        else {
            mMethodAnnotationNameRegex = null;
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.CLASS_DEF };
    }

    @Override
    public void visitToken(DetailAST aClassDefNode)
    {
        if ((isClassDefinitionAnnotated(aClassDefNode) || isAtleastOneMethodAnnotated(aClassDefNode))
                && hasUnexpectedName(aClassDefNode)) {
            logUnexpectedClassName(aClassDefNode);
        }
    }

    /**
     * Checks whether class definition annotated with user defined annotation.
     * @param aClassDefNode
     *        a class definition node
     * @return true, if class definition annotated with user defined annotation
     */
    private boolean isClassDefinitionAnnotated(DetailAST aClassDefNode)
    {
        return hasAnnotation(aClassDefNode, mClassAnnotationNameRegex);
    }

    /**
     * Checks whether class contains at least one method annotated with user
     * defined annotation.
     * @param aClassDefNode
     *        a class definition node
     * @return true, if class contains at least one method annotated with user
     *         defined annotation
     */
    private boolean isAtleastOneMethodAnnotated(DetailAST aClassDefNode)
    {
        DetailAST classMemberNode =
                aClassDefNode.findFirstToken(TokenTypes.OBJBLOCK).getFirstChild();

        while (classMemberNode != null) {
            if (classMemberNode.getType() == TokenTypes.METHOD_DEF &&
                    hasAnnotation(classMemberNode, mMethodAnnotationNameRegex))
            {
                return true;
            }

            classMemberNode = classMemberNode.getNextSibling();
        }

        return false;
    }

    /**
     * Returns true, if class has unexpected name.
     * @param aClassDefNode
     *        a class definition node
     * @return true, if class has unexpected name
     */
    private boolean hasUnexpectedName(final DetailAST aClassDefNode)
    {
        final String className = getIdentifierName(aClassDefNode);
        return !isMatchesRegex(mExpectedClassNameRegex, className);
    }

    /**
     * Returns true, if class or method has annotation with name specified in
     * regexp.
     * @param aMethodOrClassDefNode
     *        the node of type TokenTypes.METHOD_DEF or TokenTypes.CLASS_DEF
     * @param aAnnotationNamesRegexp
     *        regexp contains annotation names
     * @return true, if the class or method contains one of the annotations,
     *         specified in the regexp
     */
    private boolean hasAnnotation(DetailAST aMethodOrClassDefNode, Pattern aAnnotationNamesRegexp)
    {
        DetailAST modifierNode =
                aMethodOrClassDefNode.findFirstToken(TokenTypes.MODIFIERS).getFirstChild();

        boolean result = false;

        while (modifierNode != null) {
            if (modifierNode.getType() == TokenTypes.ANNOTATION) {
                String annotationName = getIdentifierName(modifierNode);

                if (isMatchesRegex(aAnnotationNamesRegexp, annotationName)) {
                    result = true;
                    break;
                }
            }

            modifierNode = modifierNode.getNextSibling();
        }

        return result;
    }

    /**
     * Logs unexpected class name.
     * @param aClassDef
     *        the node of type TokenTypes.CLASS_DEF
     */
    private void logUnexpectedClassName(DetailAST aClassDef)
    {
        log(aClassDef.findFirstToken(TokenTypes.IDENT), MSG_KEY, mExpectedClassNameRegex);
    }

    /**
     * Returns name of identifier contained in specified node.
     * @param aNodeWithIdent
     *        a node containing identifier or qualified identifier.
     * @return identifier name for specified node. If node contains qualified
     *         name then method returns its text representation.
     */
    private static String getIdentifierName(DetailAST aIdentifierNode)
    {
        DetailAST identNode = aIdentifierNode.findFirstToken(TokenTypes.IDENT);
        String result;

        if (identNode != null) {
            result = identNode.getText();
        }
        else {
            result = "";

            DetailAST node = aIdentifierNode.findFirstToken(TokenTypes.DOT);

            while (node.getType() == TokenTypes.DOT) {
                result = "." + node.getLastChild().getText() + result;

                node = node.getFirstChild();
            }

            result = node.getText() + result;
        }

        return result;
    }

    /**
     * Matches string against regexp.
     * @param aRegexPattern
     *        regex to match string with. May be null.
     * @param aString
     *        a string to match against regex.
     * @return false if regex is null, otherwise result of matching string
     *         against regex.
     */
    private static boolean isMatchesRegex(Pattern aRegexPattern, String aString)
    {
        if (aRegexPattern != null) {
            return aRegexPattern.matcher(aString).matches();
        }
        else {
            return false;
        }
    }
}