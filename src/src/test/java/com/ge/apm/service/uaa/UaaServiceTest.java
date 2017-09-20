package com.ge.apm.service.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import task.BaseJunit4Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UaaServiceTest {

    @Mock
    OrgInfoRepository orgInfoDao;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);



    }

    @Test
    public void testBuildOrgTree() {
        List<OrgInfo> orgInfoFullList = new ArrayList<>();

        OrgInfo orgInfo = new OrgInfo("1", "Hospital Group A", 1, null);
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("11", "Hospital AA", 2, "1");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("111", "Hospital Site AAA", 3, "11");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("1111", "Clinical AAAA", 4, "111");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("1112", "Clinical AAAB", 4, "111");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("21", "Hospital B", 2, "1");
        orgInfoFullList.add(orgInfo);


        List<OrgInfo> orgInfoClinicalDeptList = new ArrayList<>();

        orgInfo = new OrgInfo("1111", "Clinical AAAA", 4, "111");
        orgInfoClinicalDeptList.add(orgInfo);

        orgInfo = new OrgInfo("1112", "Clinical AAAB", 4, "111");
        orgInfoClinicalDeptList.add(orgInfo);



        UaaService uaaService = new UaaService();
        TreeNode rootNode = uaaService.buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoClinicalDeptList);

        //org type 1
        assertNotNull(rootNode);
        assertTrue(!rootNode.getChildren().isEmpty());
        assertEquals(1, rootNode.getChildren().size());

        //org type 2
        TreeNode secondLevelNode = rootNode.getChildren().get(0);
        assertTrue(!secondLevelNode.getChildren().isEmpty());
        assertEquals(1, secondLevelNode.getChildren().size());

        //org type 3
        TreeNode thirdLevelNode = secondLevelNode.getChildren().get(0);
        assertTrue(!thirdLevelNode.getChildren().isEmpty());
        assertEquals(2, thirdLevelNode.getChildren().size());

        //org type 4
        TreeNode fourthLevelNode1 = thirdLevelNode.getChildren().get(0);
        assertTrue(fourthLevelNode1.getChildren().isEmpty());
        assertEquals("1111", ((OrgInfo)fourthLevelNode1.getData()).getUid());

        TreeNode fourthLevelNode2 = thirdLevelNode.getChildren().get(1);
        assertTrue(fourthLevelNode2.getChildren().isEmpty());
        assertEquals("1112", ((OrgInfo)fourthLevelNode2.getData()).getUid());

    }

    @Test
    public void testBuildOrgTreeWithPartialClinicalDepts() {
        List<OrgInfo> orgInfoFullList = new ArrayList<>();

        OrgInfo orgInfo = new OrgInfo("1", "Hospital Group A", 1, null);
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("11", "Hospital AA", 2, "1");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("111", "Hospital Site AAA", 3, "11");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("1111", "Clinical AAAA", 4, "111");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("1112", "Clinical AAAB", 4, "111");
        orgInfoFullList.add(orgInfo);

        orgInfo = new OrgInfo("21", "Hospital B", 2, "1");
        orgInfoFullList.add(orgInfo);


        List<OrgInfo> orgInfoClinicalDeptList = new ArrayList<>();

        orgInfo = new OrgInfo("1111", "Clinical AAAA", 4, "111");
        orgInfoClinicalDeptList.add(orgInfo);


        UaaService uaaService = new UaaService();
        TreeNode rootNode = uaaService.buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoClinicalDeptList);

        //org type 1
        assertNotNull(rootNode);
        assertTrue(!rootNode.getChildren().isEmpty());
        assertEquals(1, rootNode.getChildren().size());

        //org type 2
        TreeNode secondLevelNode = rootNode.getChildren().get(0);
        assertTrue(!secondLevelNode.getChildren().isEmpty());
        assertEquals(1, secondLevelNode.getChildren().size());

        //org type 3
        TreeNode thirdLevelNode = secondLevelNode.getChildren().get(0);
        assertTrue(!thirdLevelNode.getChildren().isEmpty());
        assertEquals(1, thirdLevelNode.getChildren().size());

        //org type 4
        TreeNode fourthLevelNode1 = thirdLevelNode.getChildren().get(0);
        assertTrue(fourthLevelNode1.getChildren().isEmpty());
        assertEquals("1111", ((OrgInfo)fourthLevelNode1.getData()).getUid());


    }

}
