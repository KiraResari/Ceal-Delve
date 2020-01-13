package test_case_suite;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("Delve Test Case Suite")
@SelectPackages({"messaging_system", "combatants", "elements", "enemies", "messages", "server", "town", "dungeon"})
public class test_case_suite {
	
}
