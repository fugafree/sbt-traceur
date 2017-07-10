import { Man } from "./man.js";
import { Woman } from "./woman.js";

/*
try {
    let human = new Human();
} catch (error) {
    alert(error);
}
*/
const alice = new Woman("Alice", "Wonder", new Date("1980-01-06"));
const bob = new Man("Bob", "Safe", new Date("1988-05-12"));
const humans = [alice, bob];
const $list = document.querySelector("#humans-list");

humans.forEach(function(human) {
    const msg = human.sayHello();
    const $li = document.createElement("li");
    const $text = document.createTextNode(msg);

    $li.appendChild($text);
    $list.appendChild($li);
});