package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class XACDMLService {

    private final RoleConfigRepository roleRepo;
    private final WorkProductConfigRepository wpRepo;
    private final ActivityConfigRepository actRepo;
    private final XACDMLFileRepository xacdmlRepo;
    private final DeliveryProcessRepository deliveryProcessRepository;

    /**
     * Função utilitária para escapar caracteres especiais de XML.
     * Garante que os dados dinâmicos não quebrem a estrutura do arquivo.
     */
    public static String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Gera o conteúdo XACDML como String, aplicando o escape de caracteres.
     */
    @Transactional(readOnly = true)
    public String generateXACDMLContent(Long processId, String acdId) {
        DeliveryProcess process = deliveryProcessRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found with id: " + processId));
        String processName = process.getName();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>\n");
        sb.append("<!DOCTYPE acd PUBLIC  \"acd description//EN\" \"xacdml.dtd\">\n");
        sb.append("<acd id=\"").append(escapeXml(acdId)).append("\">\n");

        // Classes
        Set<String> classes = new TreeSet<>();
        List<RoleConfig> roles = roleRepo.findByDeliveryProcessId(processId);
        List<WorkProductConfig> wps = wpRepo.findByDeliveryProcessId(processId);
        roles.forEach(role -> classes.add(role.getName()));
        wps.forEach(wp -> classes.add(wp.getWorkProductName()));
        classes.forEach(clazz -> {
            sb.append("    <class id=\"").append(escapeXml(clazz)).append("\"/>\n");
        });

        // Deads
        for (RoleConfig role : roles) {
            sb.append("    <dead id=\"").append(escapeXml(role.getQueue_name())).append("\" class=\"").append(escapeXml(role.getName())).append("\">\n");
            sb.append("        <type struct=\"").append(role.getQueue_type())
                    .append("\" size=\"").append(role.getInitial_quantity())
                    .append("\" init=\"").append(role.getInitial_quantity()).append("\"/>\n");
            role.getObservers().forEach(obs ->
                    sb.append("        <observer type=\"").append(obs.getType())
                            .append("\" name=\"").append(escapeXml(obs.getName())).append("\"/>\n"));
            sb.append("    </dead>\n");
        }
        for (WorkProductConfig wp : wps) {
            sb.append("    <dead id=\"").append(escapeXml(wp.getQueue_name())).append("\" class=\"").append(escapeXml(wp.getWorkProductName())).append("\">\n");
            sb.append("        <type struct=\"").append(wp.getQueue_type())
                    .append("\" size=\"").append(wp.getQueue_size())
                    .append("\" init=\"").append(wp.getInitial_quantity()).append("\"/>\n");
            wp.getObservers().forEach(obs ->
                    sb.append("        <observer type=\"").append(obs.getType())
                            .append("\" name=\"").append(escapeXml(obs.getName())).append("\"/>\n"));
            sb.append("    </dead>\n");
        }

        // Activities
        for (WorkProductConfig wp : wps) {
            if ("OUTPUT".equalsIgnoreCase(wp.getInput_output())) continue;
            ActivityConfig act = actRepo.findByActivity(wp.getActivity());
            if (act == null) continue;
            String parentName = (act.getActivity().getSuperActivity() != null) ? act.getActivity().getSuperActivity().getName() : processName;

            sb.append("    <act id=\"").append(escapeXml(wp.getTask_name()))
                    .append("\" spem_type=\"").append(wp.getActivity().getType())
                    .append("\" dependency_type=\"").append(act.getDependencyType())
                    .append("\" processing_quantity=\"").append(act.getProcessingQuantity())
                    .append("\" condition_to_process=\"").append(act.getConditionToProcess())
                    .append("\" timebox=\"").append(act.getTimeBox())
                    .append("\" behaviour=\"").append(act.getIterationBehavior())
                    .append("\" parent=\"").append(escapeXml(parentName))
                    .append("\" need=\"").append(act.getRequiredResources()).append("\">\n");

            if (act.getDistributionType() != null && act.getDistributionParameter() != null) {
                sb.append("        <stat type=\"").append(act.getDistributionType()).append("\" ");
                switch (act.getDistributionType()) {
                    case CONSTANT -> sb.append("parm1=\"").append(act.getDistributionParameter().getConstant()).append("\" ");
                    case NORMAL -> sb.append("parm1=\"").append(act.getDistributionParameter().getAverage())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getStandardDeviation()).append("\" ");
                    case UNIFORM -> sb.append("parm1=\"").append(act.getDistributionParameter().getLow())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getHigh()).append("\" ");
                    case LOGNORMAL, GAMMA, WEIBULL -> sb.append("parm1=\"").append(act.getDistributionParameter().getScale())
                            .append("\" parm2=\"").append(act.getDistributionParameter().getShape()).append("\" ");
                    case EXPONENTIAL -> sb.append("parm1=\"").append(act.getDistributionParameter().getMean()).append("\" ");
                    case POISSON, NEGATIVE_EXPONENTIAL -> sb.append("parm1=\"").append(act.getDistributionParameter().getAverage()).append("\" ");
                    default -> sb.append("parm1=\"0.0\" ");
                }
                sb.append("/>\n");
            }

            List<RoleConfig> rolesForActivity = roleRepo.findByActivities_Id(act.getActivity().getId());
            for (RoleConfig roleConfig : rolesForActivity) {
                sb.append("        <entity_class id=\"").append(escapeXml(roleConfig.getName()))
                        .append("\" prev=\"").append(escapeXml(roleConfig.getQueue_name()))
                        .append("\" next=\"").append(escapeXml(roleConfig.getQueue_name()))
                        .append("\"/>\n");
            }

            int index = wps.indexOf(wp);
            String prevQueue = wp.getQueue_name();
            String nextQueue = (index + 1 < wps.size()) ? wps.get(index + 1).getQueue_name() : prevQueue;
            sb.append("        <entity_class id=\"").append(escapeXml(wp.getWorkProductName()))
                    .append("\" prev=\"").append(escapeXml(prevQueue))
                    .append("\" next=\"").append(escapeXml(nextQueue))
                    .append("\"/>\n");

            act.getObservers().forEach(obs ->
                    sb.append("        <observer type=\"").append(obs.getType())
                            .append("\" name=\"").append(escapeXml(obs.getName()))
                            .append("\"/>\n"));
            sb.append("    </act>\n");
        }

        sb.append("</acd>");
        return sb.toString();
    }

    /**
     * Este método agora simplesmente chama o novo método seguro para gerar o conteúdo.
     */
    @Transactional
    public XACDMLFile generateXACDML(Long processId, String acdId) {
        String content = generateXACDMLContent(processId, acdId);
        DeliveryProcess process = deliveryProcessRepository.findById(processId).orElseThrow();

        XACDMLFile file = new XACDMLFile();
        file.setProcessName(process.getName());
        file.setContent(content);
        return xacdmlRepo.save(file);
    }
}