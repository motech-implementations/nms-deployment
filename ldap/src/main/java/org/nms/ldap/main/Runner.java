package org.nms.ldap.main;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * main method of this class is executed by jar.
 *
 */
public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        Resource resource = PropertyLoader.initializeResource();
        Map<String, List<String>> dataMap = NmsDataHelper
                .getStateDistrictData(resource);
        if (resource != null && !dataMap.isEmpty()) {
            try {
                LOGGER.info("LDAP connection Initialize...");
                LdapIntegrator.initializeLdapConnection(resource);

                // iterate data Map
                for (String state : dataMap.keySet()) {
                    List<String> districtList = dataMap.get(state);
                    addUsersDirectoryEntry(state, districtList);
                    addRolesDirectoryEntryForNationalView(state, districtList);
                    addRolesDirectoryEntryForAdminView(state, districtList);
                    addUserAdminAccessACI(state);
                    addUserACI(state, districtList);
                    addViewerAccessACI(state, districtList);
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                // close LDAP connection
                LdapIntegrator.closeLdapConnection();
                LOGGER.info("LDAP connection Closed");
            }
        } else {
            LOGGER.error("Error occured while loading resource or fetching reporting data");
        }

    }

    /**
     * add OU=Users Directory Entry
     * 
     * @param state
     * @param districtList
     */
    private static void addUsersDirectoryEntry(String state,
            List<String> districtList) {
        String stateDn = String.format("cn=%s,ou=users,dc=nms", state);
        LdapIntegrator.addentry(stateDn, "objectclass: organizationalRole",
                "objectclass: top");
        for (String district : districtList) {
            String districtDn = String.format("cn=%s,cn=%s,ou=users,dc=nms",
                    district, state);
            LdapIntegrator.addentry(districtDn,
                    "objectclass: organizationalRole", "objectclass: top");

        }
    }

    /**
     * add OU=Roles Directory Entry for National View
     * 
     * @param state
     * @param districtList
     */
    private static void addRolesDirectoryEntryForNationalView(String state,
            List<String> districtList) {
        String stateDn = String.format(
                "cn=%s View,cn=National View,ou=roles,dc=nms", state);
        String stateroleOccupant = String.format("cn=%s,cn=%s,ou=users,dc=nms",
                state, state);
        LdapIntegrator.addentry(stateDn, "objectclass: organizationalRole",
                "objectclass: top", "roleOccupant", stateroleOccupant);
        for (String district : districtList) {
            String districtDn = String.format(
                    "cn=%s View,cn=%s View,cn=National View,ou=roles,dc=nms",
                    district, state);
            String districtRoleOccupant = String.format(
                    "cn=%s,cn=%s,cn=%s,ou=users,dc=nms", district, district,
                    state);
            LdapIntegrator.addentry(districtDn,
                    "objectclass: organizationalRole", "objectclass: top",
                    "roleOccupant", districtRoleOccupant);

        }
    }

    /**
     * add OU=Roles Directory Entry for Administrator View
     * 
     * @param state
     * @param districtList
     */
    private static void addRolesDirectoryEntryForAdminView(String state,
            List<String> districtList) {
        String stateDn = String
                .format("cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms",
                        state);
        String stateMember = String.format("cn=%s_admin,cn=%s,ou=users,dc=nms",
                state, state);
        LdapIntegrator.addentry(stateDn, "objectclass: groupOfNames",
                "objectclass: top", "member", stateMember);
        for (String district : districtList) {
            String districtDn = String
                    .format("cn=%s User Administrator,cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms",
                            district, state);
            String districtMember = String.format(
                    "cn=%s_admin,cn=%s,cn=%s,ou=users,dc=nms", district,
                    district, state);
            LdapIntegrator.addentry(districtDn, "objectclass: groupOfNames",
                    "objectclass: top", "member", districtMember);

        }
    }

    /**
     * Add UserAdminAccessACI for state
     * 
     * @param state
     */
    private static void addUserAdminAccessACI(String state) {
        String dn = String.format("cn=%sUserAdminAccessACI,dc=nms", state);
        String prescriptiveACIAttr = String
                .format("{ identificationTag \"%sUserAdminAccessACI\", precedence 0, authenticationLevel simple, itemOrUserFirst userFirst: { userClasses { userGroup { \"cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms\" } }, userPermissions { { protectedItems { entry, allUserAttributeTypesAndValues }, grantsAndDenials { grantModify, grantReturnDN, grantRemove, grantInvoke, grantExport, grantAdd, grantRename, grantImport, grantRead, grantFilterMatch, grantBrowse, grantCompare, grantDiscloseOnError } } } } }",
                        state, state);
        String subTreeSpecificationAttr = String
                .format("{ base \"cn=%s User Administrator,cn=National User Administrator,ou=roles\", minimum 1 }",
                        state);
        LdapIntegrator.addentry(dn, "objectclass: subentry",
                "objectclass: top", "prescriptiveACI", prescriptiveACIAttr,
                "subtreeSpecification", subTreeSpecificationAttr);
    }

    /**
     * Add UserACI for state and districts
     * 
     * @param state
     * @param districtList
     */
    private static void addUserACI(String state, List<String> districtList) {
        String dn = String.format("cn=%sUserACI,dc=nms", state);
        String prescriptiveACIAttr = String
                .format("{ identificationTag \"%sUserACI\", precedence 0, authenticationLevel simple, itemOrUserFirst userFirst: { userClasses { userGroup { \"cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms\" } }, userPermissions { { protectedItems { entry, allUserAttributeTypesAndValues }, grantsAndDenials { grantInvoke, grantRemove, grantDiscloseOnError, grantExport, grantReturnDN, grantModify, grantRename, grantRead, grantCompare, grantBrowse, grantFilterMatch, grantImport, grantAdd } } }} }",
                        state, state);
        String subTreeSpecificationAttr = String.format(
                "{ base \"cn=%s,ou=users\" }", state);
        LdapIntegrator.addentry(dn, "objectclass: subentry",
                "objectclass: accessControlSubentry", "objectclass: top",
                "prescriptiveACI", prescriptiveACIAttr, "subtreeSpecification",
                subTreeSpecificationAttr);
        for (String district : districtList) {
            String disDn = String.format("cn=%sUserACI,dc=nms", district);
            String distPrescriptiveACIAttr = String
                    .format("{ identificationTag \"%sUserACI\", precedence 0, authenticationLevel simple, itemOrUserFirst userFirst: { userClasses { userGroup { \"cn=%s User Administrator,cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms\" } }, userPermissions {{ protectedItems { entry, allUserAttributeTypesAndValues }, grantsAndDenials { grantDiscloseOnError, grantRename, grantModify, grantAdd, grantRead, grantInvoke, grantImport, grantReturnDN, grantFilterMatch, grantCompare, grantExport, grantBrowse, grantRemove } } } } }",
                            district, district, state);
            String disSubTreeSpecificationAttr = String.format(
                    "{ base \"cn=%s,cn=%s,ou=users\" }", district, state);
            LdapIntegrator.addentry(disDn, "objectclass: subentry",
                    "objectClass: accessControlSubentry", "objectclass: top",
                    "prescriptiveACI", distPrescriptiveACIAttr,
                    "subtreeSpecification", disSubTreeSpecificationAttr);

        }
    }

    /**
     * Add Viewer Access ACI for state and districts
     * 
     * @param state
     * @param districtList
     */
    private static void addViewerAccessACI(String state,
            List<String> districtList) {
        String dn = String.format("cn=%sViewerAccessACI,dc=nms", state);
        String prescriptiveACIAttr = String
                .format("{ identificationTag \"%sViewerAccessACI\", precedence 0, authenticationLevel simple, itemOrUserFirst userFirst: { userClasses {userGroup { \"cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms\" } }, userPermissions { { protectedItems { entry, allUserAttributeTypesAndValues }, grantsAndDenials { grantInvoke, grantRemove, grantDiscloseOnError, grantExport, grantReturnDN, grantModify, grantRename, grantRead, grantCompare, grantBrowse, grantFilterMatch, grantImport, grantAdd } } } } }",
                        state, state);
        String subTreeSpecificationAttr = String.format(
                "{ base \"cn=%s View,cn=National View,ou=roles\"}", state);
        LdapIntegrator.addentry(dn, "objectclass: subentry",
                "objectclass: accessControlSubentry", "objectclass: top",
                "prescriptiveACI", prescriptiveACIAttr, "subtreeSpecification",
                subTreeSpecificationAttr);
        for (String district : districtList) {
            String disDn = String.format("cn=%sViewerAccessACI,dc=nms",
                    district);
            String distPrescriptiveACIAttr = String
                    .format("{ identificationTag \"%sViewerAccessACI\", precedence 0,authenticationLevel simple, itemOrUserFirst userFirst: { userClasses { userGroup { \"cn=%s User Administrator,cn=%s User Administrator,cn=National User Administrator,ou=roles,dc=nms\" } }, userPermissions { { protectedItems { entry, allUserAttributeTypesAndValues }, grantsAndDenials { grantCompare, grantModify, grantFilterMatch, grantRename, grantRemove, grantExport, grantInvoke, grantReturnDN, grantImport, grantDiscloseOnError, grantRead, grantBrowse, grantAdd } } } } }",
                            district, district, state);
            String disSubTreeSpecificationAttr = String
                    .format("{ base \"cn=%s View,cn=%s View,cn=National View,ou=roles\" }",
                            district, state);
            LdapIntegrator.addentry(disDn, "objectclass: subentry",
                    "objectClass: accessControlSubentry", "objectclass: top",
                    "prescriptiveACI", distPrescriptiveACIAttr,
                    "subtreeSpecification", disSubTreeSpecificationAttr);

        }
    }
}
