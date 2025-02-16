// utils/indexedDBUtils.js
import { openDB } from 'idb';

export const saveAccessTokenToDB = async (accessToken) => {
  const db = await openDB('my-db', 1, {
    upgrade(db) {
      db.createObjectStore('tokens');
    },
  });
  await db.put('tokens', accessToken, 'accessToken');
  await db.close();
};

export const getAccessTokenFromDB = async () => {
  const db = await openDB('my-db', 1);
  const token = await db.get('tokens', 'accessToken');
  await db.close();
  return token;
};

export const deleteAccessTokenFromDB = async () => {
  const db = await openDB('my-db', 1);
  await db.delete('tokens', 'accessToken');
  await db.close();
};