package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.enums.BestFitDistribution;
import com.example.projeto_tcc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class XACDMLService {

    private final RoleConfigRepository roleRepo;
    private final WorkProductConfigRepository wpRepo;
    private final ActivityConfigRepository actRepo;
    private final XACDMLFileRepository xacdmlRepo;
    private final DeliveryProcessRepository deliveryProcessRepository;

    @Transactional
    public XACDMLFile generateXACDML(Long processId, String acdId) {
        // Buscar o processo no banco pelo ID
        DeliveryProcess process = deliveryProcessRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        String processName = process.getName(); // pega o nome real do processo

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>\n");
        sb.append("<!DOCTYPE acd PUBLIC  \"acd description//EN\" \"xacdml.dtd\">\n");
        sb.append("<acd id=\"").append(acdId).append("\">\n");

        // ---------------- Classes ----------------
        Set<String> classes = new TreeSet<>();

        List<RoleConfig> roles = roleRepo.findByDeliveryProcessId(processId);
        List<WorkProductConfig> wps = wpRepo.findByDeliveryProcessId(processId);

        roles.forEach(role -> classes.add(role.getName()));
        wps.forEach(wp -> classes.add(wp.getWorkProductName()));

        classes.forEach(clazz -> sb.append("    <class id=\"").append(clazz).append("\"/>\n"));

        // ---------------- Deads ----------------
        for (RoleConfig role : roles) {
            sb.append("    <dead id=\"").append(role.getQueue_name()).append("\" class=\"").append(role.getName()).append("\">\n");
            sb.append("        <type struct=\"").append(role.getQueue_type())
                    .append("\" size=\"").append(role.getInitial_quantity())
                    .append("\" init=\"").append(role.getInitial_quantity()).append("\"/>\n");
            role.getObservers().forEach(obs ->
                    sb.append("        <observer type=\"").append(obs.getType())
                            .append("\" name=\"").append(obs.getName()).append("\"/>\n"));
            sb.append("    </dead>\n");
        }

        for (WorkProductConfig wp : wps) {
            sb.append("    <dead id=\"").append(wp.getQueue_name()).append("\" class=\"").append(wp.getWorkProductName()).append("\">\n");
            sb.append("        <type struct=\"").append(wp.getQueue_type())
                    .append("\" size=\"").append(wp.getQueue_size())
                    .append("\" init=\"").append(wp.getInitial_quantity()).append("\"/>\n");
            wp.getObservers().forEach(obs ->
                    sb.append("        <observer type=\"").append(obs.getType())
                            .append("\" name=\"").append(obs.getName()).append("\"/>\n"));
            sb.append("    </dead>\n");
        }

        // ---------------- Activities ----------------
        for (WorkProductConfig wp : wps) {
            // Ignora o OUTPUT
            if ("OUTPUT".equalsIgnoreCase(wp.getInput_output())) continue;

            ActivityConfig act = actRepo.findByActivity(wp.getActivity());
            sb.append("    <act id=\"").append(wp.getTask_name())
                    .append("\" spem_type=\"").append(wp.getActivity().getType())
                    .append("\" dependency_type=\"").append(act.getDependencyType())
                    .append("\" processing_quantity=\"").append(act.getProcessingQuantity())
                    .append("\" condition_to_process=\"").append(act.getConditionToProcess())
                    .append("\" timebox=\"").append(act.getTimeBox())
                    .append("\" behaviour=\"").append(act.getIterationBehavior())
                    .append("\" parent=\"").append(act.getActivity().getSuperActivity() != null ? act.getActivity().getSuperActivity().getName() : processName)
                    .append("\" need=\"").append(act.getRequiredResources()).append("\">\n");

            // ---------------- Stat ----------------
            if (act.getDistributionType() != null && act.getDistributionParameter() != null) {
                sb.append("        <stat type=\"").append(act.getDistributionType()).append("\" ");
                switch (act.getDistributionType()) {
                    case CONSTANT -> sb.append("parm1=\"").append(act.getDistributionParameter().getConstant()).append("\" ");
                    case NORMAL -> sb.append("parm1=\"").append(act.getDistributionParameter().getMean())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getStandardDeviation()).append("\" ");
                    case UNIFORM -> sb.append("parm1=\"").append(act.getDistributionParameter().getMin())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getMax()).append("\" ");
                    case LOGNORMAL, GAMMA -> sb.append("parm1=\"").append(act.getDistributionParameter().getScale())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getShape()).append("\" ");
                    case WEIBULL -> sb.append("parm1=\"").append(act.getDistributionParameter().getBeta())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getAlpha()).append("\" ");
                    case EXPONENTIAL -> sb.append("parm1=\"").append(act.getDistributionParameter().getMean()).append("\" ");
                    case POISSON -> sb.append("parm1=\"").append(act.getDistributionParameter().getLambda()).append("\" ");
                    case GEOMETRIC -> sb.append("parm1=\"").append(act.getDistributionParameter().getProbability()).append("\" ");
                    default -> sb.append("parm1=\"0.0\" ");
                }
                sb.append("/>\n");
            }

            // ---------------- Entity Classes ----------------
            List<RoleConfig> rolesForActivity = roleRepo.findByActivities_Id(act.getActivity().getId());
            for (RoleConfig roleConfig : rolesForActivity) {
                sb.append("        <entity_class id=\"")
                        .append(roleConfig.getName())
                        .append("\" prev=\"").append(roleConfig.getQueue_name())
                        .append("\" next=\"").append(roleConfig.getQueue_name())
                        .append("\"/>\n");
            }

            // WorkProduct prev/next sequenciais dentro do mesmo processo
            int index = wps.indexOf(wp);
            String prevQueue = wp.getQueue_name();
            String nextQueue = (index + 1 < wps.size()) ? wps.get(index + 1).getQueue_name() : prevQueue;
            sb.append("        <entity_class id=\"").append(wp.getWorkProductName())
                    .append("\" prev=\"").append(prevQueue)
                    .append("\" next=\"").append(nextQueue)
                    .append("\"/>\n");

            // ---------------- Observers ----------------
            act.getObservers().forEach(obs ->
                    sb.append("        <observer type=\"").append(obs.getType())
                            .append("\" name=\"").append(obs.getName()).append("\"/>\n"));

            sb.append("    </act>\n");
        }

        sb.append("</acd>");

        // ---------------- Salva no banco ----------------
        XACDMLFile file = new XACDMLFile();
        file.setProcessName(processName);
        file.setContent(sb.toString());
        return xacdmlRepo.save(file);
    }


}
