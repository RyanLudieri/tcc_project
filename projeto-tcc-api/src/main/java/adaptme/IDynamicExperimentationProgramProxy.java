package adaptme;

import simula.manager.SimulationManager;

public interface IDynamicExperimentationProgramProxy {

    void execute(float simulationDuration);

    void setSimulationManager(SimulationManager simulationManager);

    void setSimulationDuration(float simulationDuration);

    float getSimulationDuration();

    SimulationManager getSimulationManager();

}