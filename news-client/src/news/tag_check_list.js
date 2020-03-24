import React from 'react';
import $ from 'jquery'

const TAG_URL = 'http://localhost:8080/news-manager/tag'

export default class TagCheckList extends React.Component {
    constructor() {
        super()
        this.state = {
            isLoaded: false,
            isUpdateNeeded: true,
            error: null,
            tags: []
        }
        this.tag_check_list = React.createRef();
    }

    updateData() {
        $.ajax(
            {
                method: 'GET',
                url: TAG_URL
            }
        ).fail(
            (responce) => {
                this.setState(
                    {
                        isLoaded: false,
                        tags: [],
                        isUpdateNeeded: false,
                        error: {
                            code: responce.statusCode,
                            text: responce.statusText,
                            comment: null
                        }
                    }
                )
            }
        ).done(
            (responce) => {
                this.setState(
                    {
                        tags: responce,
                        isUpdateNeeded: false,
                        isLoaded: true,
                        error: null
                    }
                )
            }
        )
    }

    setUpdateNeeded() {
        this.setState(
            {
                tags: this.state.tags,
                isUpdateNeeded: true,
                isLoaded: this.state.isUpdateNeeded,
                error: this.state.error
            }
        )
    }

    render() {

        
        if (this.state.isUpdateNeeded) {
            this.updateData()
        }

        if (this.state.isLoaded) {
            return (
                <div class='mdl-grid' id="tags-checkbox-list">
                    
                    {
                        this.state.tags.map((tag, i) => {

                            if (this.props.selectedTags.some(e => e.name === tag.name)) {
                                return (
                                        <div class='mdl-cell mdl-cell--3-col'>
                                            <label class = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" 
                                                for = {tag.name + '-tag-checkbox'}>
                                                <input type = "checkbox" id = {tag.name + '-tag-checkbox'} class = "mdl-checkbox__input" checked/>
                                                <span class = "mdl-checkbox__label">{tag.name}</span>
                                            </label>	  
                                        </div>
                                    
                                )

                            } else {
                                return (
                                    <div class='mdl-cell mdl-cell--3-col'>
                                        <label class = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" 
                                            for = {tag.name + '-tag-checkbox'}>
                                            <input type = "checkbox" id = {tag.name + '-tag-checkbox'} class = "mdl-checkbox__input"/>
                                            <span class = "mdl-checkbox__label">{tag.name}</span>
                                        </label>	  
                                    </div>
                                
                            )
                            }
                        })
                    }
                    
                </div>
            )
        } else {
            if (this.state.error !== null) {
                return (
                    <div>
                        <span>Error while loading tags</span>
                        <span>Status code: {this.state.error.code}</span> 
                        <span>Text: {this.state.error.text}</span> 
                        <span>Comment: {this.state.error.comment}</span> 
                    </div>
                )
            } else {
                return (
                    <span>
                        Loading tags...
                    </span>
                )
            }
        }

    }

    getSelected() {
        let result = []
        this.state.tags.forEach(tag => {
            if ($("#" + tag.name + '-tag-checkbox').is(":checked")) {
                result.push(tag)
            }
        })
        return result;
    }

}