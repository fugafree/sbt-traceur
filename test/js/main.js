System.registerModule("../src/main/assets/js/actions.js", [], function() {
  "use strict";
  var __moduleName = "../src/main/assets/js/actions.js";
  var Actions = function() {
    function Actions() {
      this.actions = [];
      this.behaviors = [];
    }
    return ($traceurRuntime.createClass)(Actions, {
      getActions: function() {
        return this.actions;
      },
      getBehaviors: function() {
        return this.behaviors;
      }
    }, {});
  }();
  return {get Actions() {
      return Actions;
    }};
});
System.registerModule("../src/main/assets/js/account.js", [], function() {
  "use strict";
  var __moduleName = "../src/main/assets/js/account.js";
  var Actions = System.get("../src/main/assets/js/actions.js").Actions;
  var AccountActions = function($__super) {
    function AccountActions() {
      $traceurRuntime.superConstructor(AccountActions).call(this);
      this.actions = [{
        type: "ACTION_STARTED",
        action: function(state) {
          return Object.assign({}, state, {});
        }
      }, {
        type: "ACTION_COMPLETED",
        action: function(state, action) {
          return Object.assign({}, state, {answer: action.answer});
        }
      }];
      this.behaviors = [{
        name: "getAction",
        function: function() {
          return function(dispatch) {
            dispatch({type: "ACTION_STARTED"});
            setTimeout(function() {
              dispatch({
                type: "ACTION_COMPLETED",
                action: {answer: "42"}
              });
            }, 5000);
          };
        }
      }];
    }
    return ($traceurRuntime.createClass)(AccountActions, {}, {}, $__super);
  }(Actions);
  return {get AccountActions() {
      return AccountActions;
    }};
});
System.registerModule("../src/main/assets/js/main.js", [], function() {
  "use strict";
  var __moduleName = "../src/main/assets/js/main.js";
  var AccountActions = System.get("../src/main/assets/js/account.js").AccountActions;
  var actions = new AccountActions();
  var actionsName = JSON.stringify(actions.getActions());
  console.log(("actions: " + actionsName));
  return {};
});
System.get("../src/main/assets/js/main.js" + '');
