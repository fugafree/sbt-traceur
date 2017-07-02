import { AccountActions } from "./account.js";

let actions = new AccountActions();
let actionsName = JSON.stringify(actions.getActions());

console.log(`actions: ${actionsName}`);