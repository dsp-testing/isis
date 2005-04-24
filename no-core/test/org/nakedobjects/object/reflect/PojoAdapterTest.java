package org.nakedobjects.object.reflect;

import org.nakedobjects.NakedObjects;
import org.nakedobjects.object.DummyNakedObjectSpecificationLoader;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.defaults.MockNakedObjectSpecificationLoader;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PojoAdapterTest extends TestCase {

    private PojoAdapterFactoryImpl pojoAdapterFactory;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PojoAdapterTest.class);
    }

    protected void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);
		pojoAdapterFactory = new PojoAdapterFactoryImpl();
		pojoAdapterFactory.setPojoAdapterHash(new PojoAdapterHashImpl());
        pojoAdapterFactory.setReflectorFactory(new DummyReflectorFactory());
    }

    public void testTitleStringWhereThereIsNoSpecification() {
        NakedObjects.setSpecificationLoader(new DummyNakedObjectSpecificationLoader());
        NakedObject pa = pojoAdapterFactory.createNOAdapter(new TestPojo());
        assertEquals("",  pa.titleString());
    }

    public void testTitleStringWhereSpecificationProvidesTitleFromObject() {
        MockNakedObjectSpecificationLoader specLoader = new MockNakedObjectSpecificationLoader();
        specLoader.addSpec(new NakedObjectSpecificationProvidingTitle());
        NakedObjects.setSpecificationLoader(specLoader);
        NakedObject pa = pojoAdapterFactory.createNOAdapter(new TestPojo());
        pa.getSpecification();
        pa.setResolved();
        assertEquals("object title from specification",  pa.titleString());
    }
    
    public void testTitleFromUnresolvedObject() {
        MockNakedObjectSpecificationLoader specLoader = new MockNakedObjectSpecificationLoader();
        specLoader.addSpec(new NakedObjectSpecificationProvidingNullTitle());
        NakedObjects.setSpecificationLoader(specLoader);
        NakedObject pa = pojoAdapterFactory.createNOAdapter(new TestPojo());
        pa.getSpecification();
        assertFalse(pa.isResolved());
        assertEquals("unresolved title",  pa.titleString());
    }
    
    public void testTitleStringWhereSpecificationReturnNullAsTitle() {
        MockNakedObjectSpecificationLoader specLoader = new MockNakedObjectSpecificationLoader();
        specLoader.addSpec(new NakedObjectSpecificationProvidingNullTitle());
        NakedObjects.setSpecificationLoader(specLoader);
        NakedObject pa = pojoAdapterFactory.createNOAdapter(new TestPojo());
        pa.getSpecification();
        pa.setResolved();
        assertEquals("A singlar name from specification",  pa.titleString());
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/