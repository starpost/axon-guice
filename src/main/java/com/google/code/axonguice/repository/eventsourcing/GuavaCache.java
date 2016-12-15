package com.google.code.axonguice.repository.eventsourcing;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.axonframework.cache.Cache;

import com.google.common.cache.CacheBuilder;

public class GuavaCache implements Cache {

	final com.google.common.cache.Cache<Object, Object> backend;

	public GuavaCache() {
		this.backend = CacheBuilder.newBuilder().build();
	}

	public GuavaCache(com.google.common.cache.Cache<Object, Object> backend) {
		this.backend = backend;
	}

	public GuavaCache(long ttl, TimeUnit timeunit) {
		this.backend = CacheBuilder.newBuilder().expireAfterAccess(ttl, timeunit).build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> V get(K key) {
		V returnValue = (V) backend.getIfPresent(key);
		if (returnValue != null) {
			for (EntryListener adapter : adapters) {
				adapter.onEntryRead(key, returnValue);
			}
		}
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> void put(K key, V value) {
		V previous = (V) backend.getIfPresent(key);
		backend.put(key, value);
		if (previous != null) {
			for (EntryListener adapter : adapters) {
				adapter.onEntryUpdated(key, value);
			}
		} else {
			for (EntryListener adapter : adapters) {
				adapter.onEntryCreated(key, value);
			}
		}
	}

	@Override
	public <K, V> boolean putIfAbsent(K key, V value) {
		if (backend.getIfPresent(key) == null) {
			backend.put(key, value);
			for (EntryListener adapter : adapters) {
				adapter.onEntryCreated(key, value);
			}
			return true;
		}
		return false;
	}

	@Override
	public <K> boolean remove(K key) {
		if (backend.getIfPresent(key) != null) {
			backend.invalidate(key);
			for (EntryListener adapter : adapters) {
				adapter.onEntryRemoved(key);
			}
			return true;
		}
		return false;
	}

	@Override
	public <K> boolean containsKey(K key) {
		return backend.getIfPresent(key) != null;
	}

	private final Set<EntryListener> adapters = new CopyOnWriteArraySet<EntryListener>();

	@Override
	public void registerCacheEntryListener(EntryListener entryListener) {
		this.adapters.add(entryListener);
	}

	@Override
	public void unregisterCacheEntryListener(EntryListener entryListener) {
		this.adapters.remove(entryListener);
	}

}
