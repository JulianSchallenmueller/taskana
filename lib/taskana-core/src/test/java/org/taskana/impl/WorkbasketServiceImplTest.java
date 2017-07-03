package org.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.exceptions.WorkbasketNotFoundException;
import org.taskana.model.Workbasket;
import org.taskana.model.WorkbasketAccessItem;
import org.taskana.model.mappings.DistributionTargetMapper;
import org.taskana.model.mappings.WorkbasketAccessMapper;
import org.taskana.model.mappings.WorkbasketMapper;

@RunWith(MockitoJUnitRunner.class)
public class WorkbasketServiceImplTest {

	@InjectMocks
	WorkbasketServiceImpl workbasketServiceImpl;

	@Mock
	WorkbasketMapper workbasketMapper;
	@Mock
	DistributionTargetMapper distributionTargetMapper;
	@Mock
	WorkbasketAccessMapper workbasketAccessMapper;

	@Test
	public void testInsertWorkbasket() throws NotAuthorizedException {
		doNothing().when(workbasketMapper).insert(any());
		Workbasket workbasket = new Workbasket();
		workbasket.setId("1");
		workbasketServiceImpl.createWorkbasket(workbasket);

		Assert.assertEquals("1", workbasket.getId());
	}

	@Test
	public void testSelectAllWorkbaskets() throws NotAuthorizedException {
		doNothing().when(workbasketMapper).insert(any());

		Workbasket workbasket0 = new Workbasket();
		workbasket0.setId("0");
		workbasketServiceImpl.createWorkbasket(workbasket0);
		Workbasket workbasket1 = new Workbasket();
		workbasket1.setId("1");
		workbasketServiceImpl.createWorkbasket(workbasket1);
		Workbasket workbasket2 = new Workbasket();
		workbasket2.setId("2");
		workbasketServiceImpl.createWorkbasket(workbasket2);

		verify(workbasketMapper, atLeast(3)).insert(any());
	}

	@Test
	public void testSelectWorkbasket() throws WorkbasketNotFoundException, NotAuthorizedException {
		doNothing().when(workbasketMapper).insert(any());

		Workbasket workbasket0 = new Workbasket();
		workbasket0.setId("0");
		workbasketServiceImpl.createWorkbasket(workbasket0);
		Workbasket workbasket1 = new Workbasket();
		workbasket1.setId("1");
		workbasketServiceImpl.createWorkbasket(workbasket1);
		Workbasket workbasket2 = new Workbasket();
		workbasket2.setId("2");
		workbasketServiceImpl.createWorkbasket(workbasket2);

		verify(workbasketMapper, atLeast(3)).insert(any());

		when(workbasketMapper.findById(any())).thenReturn(workbasket2);

		Workbasket foundWorkbasket = workbasketServiceImpl.getWorkbasket("2");
		Assert.assertEquals("2", foundWorkbasket.getId());
	}

	@Test(expected = WorkbasketNotFoundException.class)
	public void testGetWorkbasketFail() throws WorkbasketNotFoundException {
		workbasketServiceImpl.getWorkbasket("fail");
	}

	@Test
	public void testSelectWorkbasketWithDistribution() throws WorkbasketNotFoundException, NotAuthorizedException {
		doNothing().when(workbasketMapper).insert(any());

		Workbasket workbasket0 = new Workbasket();
		workbasket0.setId("0");
		Workbasket workbasket1 = new Workbasket();
		workbasket1.setId("1");
		Workbasket workbasket2 = new Workbasket();
		workbasket2.setId("2");
		workbasket2.setDistributionTargets(new ArrayList<>());
		workbasket2.getDistributionTargets().add(workbasket0);
		workbasket2.getDistributionTargets().add(workbasket1);
		workbasketServiceImpl.createWorkbasket(workbasket2);

		when(workbasketMapper.findById(any())).thenReturn(workbasket2);

		Workbasket foundWorkbasket = workbasketServiceImpl.getWorkbasket("2");
		Assert.assertEquals("2", foundWorkbasket.getId());
		Assert.assertEquals(2, foundWorkbasket.getDistributionTargets().size());
	}

	@Test
	public void testUpdateWorkbasket() throws Exception {
		doNothing().when(workbasketMapper).insert(any());

		Workbasket workbasket0 = new Workbasket();
		workbasket0.setId("0");
		Workbasket workbasket1 = new Workbasket();
		workbasket1.setId("1");
		Workbasket workbasket2 = new Workbasket();
		workbasket2.setId("2");
		workbasket2.getDistributionTargets().add(workbasket0);
		workbasket2.getDistributionTargets().add(workbasket1);
		workbasketServiceImpl.createWorkbasket(workbasket2);

		Workbasket workbasket3 = new Workbasket();
		workbasket3.setId("3");
		workbasket2.getDistributionTargets().clear();
		workbasket2.getDistributionTargets().add(workbasket3);
		Thread.sleep(100);

		doNothing().when(workbasketMapper).update(any());
		workbasketServiceImpl.updateWorkbasket(workbasket2);

		when(workbasketMapper.findById("2")).thenReturn(workbasket2);
		Workbasket foundBasket = workbasketServiceImpl.getWorkbasket(workbasket2.getId());
		
		when(workbasketMapper.findById("1")).thenReturn(workbasket1);
		when(workbasketMapper.findById("3")).thenReturn(workbasket1);
		
		List<Workbasket> distributionTargets = foundBasket.getDistributionTargets();
		Assert.assertEquals(1, distributionTargets.size());
		Assert.assertEquals("3", distributionTargets.get(0).getId());
		
		Assert.assertNotEquals(workbasketServiceImpl.getWorkbasket("2").getCreated(), workbasketServiceImpl.getWorkbasket("2").getModified());
		Assert.assertEquals(workbasketServiceImpl.getWorkbasket("1").getCreated(), workbasketServiceImpl.getWorkbasket("1").getModified());
		Assert.assertEquals(workbasketServiceImpl.getWorkbasket("3").getCreated(), workbasketServiceImpl.getWorkbasket("3").getModified());
	}

	@Test
	public void testInsertWorkbasketAccessUser() throws NotAuthorizedException {
		doNothing().when(workbasketAccessMapper).insert(any());

		WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
		accessItem.setWorkbasketId("1");
		accessItem.setUserId("Arthur Dent");
		accessItem.setOpen(true);
		accessItem.setRead(true);
		accessItem = workbasketServiceImpl.createWorkbasketAuthorization(accessItem);

		Assert.assertNotNull(accessItem.getId());
	}

	@Test
	public void testUpdateWorkbasketAccessUser() throws NotAuthorizedException {
		doNothing().when(workbasketAccessMapper).insert(any());

		WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
		accessItem.setWorkbasketId("1");
		accessItem.setUserId("Arthur Dent");
		accessItem.setOpen(true);
		accessItem.setRead(true);
		accessItem = workbasketServiceImpl.createWorkbasketAuthorization(accessItem);

		Assert.assertNotNull(accessItem.getId());

		doNothing().when(workbasketAccessMapper).update(any());
		accessItem.setUserId("Zaphod Beeblebrox");
		workbasketServiceImpl.updateWorkbasketAuthorization(accessItem);

		Assert.assertEquals("Zaphod Beeblebrox", accessItem.getUserId());
	}

}
