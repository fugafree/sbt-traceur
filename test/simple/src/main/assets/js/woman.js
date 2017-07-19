import { Human } from "./human.js";

export class Woman extends Human {

    constructor(firstname, lastname, birthday) {
        super(firstname, lastname, birthday);
        this.sex = false;
    }

}