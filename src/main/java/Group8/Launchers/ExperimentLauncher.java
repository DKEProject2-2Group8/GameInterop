package Group8.Launchers;

import Group8.Experiments.AgentCountTest.AgentCountExperiment;
import Group8.Experiments.ParametersTest.ParameterTests;

public class ExperimentLauncher {

    public static void main(String[] args) {
        //AgentCountExperiment.runTest(true);
        //ParameterTests.runTests(true);
        AgentCountExperiment.executeTests(true);
    }
}
