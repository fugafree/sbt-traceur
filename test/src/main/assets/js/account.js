import { Actions } from "./actions.js";

export class AccountActions extends Actions {

    constructor() {
        super();
        this.actions = [
            {type: "ACTION_STARTED", action: (state) => {
                return Object.assign({}, state, {});
            }},
            {type: "ACTION_COMPLETED", action: (state, action) => {
                return Object.assign({}, state, {answer: action.answer});
            }}
        ];
        this.behaviors = [
            {name: "getAction", function: function() {
                return function(dispatch) {
                    dispatch({type: "ACTION_STARTED"});
                    setTimeout(() => {
                        dispatch({type: "ACTION_COMPLETED", action: {answer: "42"}});
                    }, 5000);
                }
            }}
        ];
    }
}