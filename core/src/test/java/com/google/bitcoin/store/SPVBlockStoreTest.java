/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.bitcoin.store;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.StoredBlock;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class SPVBlockStoreTest {

    @Test
    public void basics() throws Exception {
        NetworkParameters params = NetworkParameters.unitTests();
        File f = File.createTempFile("spvblockstore", null);
        f.delete();
        f.deleteOnExit();
        SPVBlockStore store = new SPVBlockStore(params, f);

        Address to = new ECKey().toAddress(params);
        // Check the first block in a new store is the genesis block.
        StoredBlock genesis = store.getChainHead();
        assertEquals(params.genesisBlock, genesis.getHeader());
        assertEquals(0, genesis.getHeight());


        // Build a new block.
        StoredBlock b1 = genesis.build(genesis.getHeader().createNextBlock(to).cloneAsHeader());
        store.put(b1);
        store.setChainHead(b1);
        store.close();

        // Check we can get it back out again if we rebuild the store object.
        store = new SPVBlockStore(params, f);
        StoredBlock b2 = store.get(b1.getHeader().getHash());
        assertEquals(b1, b2);
        // Check the chain head was stored correctly also.
        StoredBlock chainHead = store.getChainHead();
        assertEquals(b1, chainHead);
    }
}
