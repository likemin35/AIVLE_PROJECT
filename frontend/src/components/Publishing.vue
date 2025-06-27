<template>
    <div>
        <String
            label="표지 이미지"
            v-model="value.image"
            :editMode="editMode"
        />
        <String
            label="요약 줄거리"
            v-model="value.summaryContent"
            :editMode="editMode"
        />
        <String
            label="도서명"
            v-model="value.bookName"
            :editMode="editMode"
        />
        <String
            label="PDF Path"
            v-model="value.pdfPath"
            :editMode="editMode"
        />
        <String
            label="작가정보"
            v-model="value.authorId"
            :editMode="editMode"
        />
        <String
            label="도서 URL"
            v-model="value.webUrl"
            :editMode="editMode"
        />
        <String
            label="카테고리"
            v-model="value.category"
            :editMode="editMode"
        />
        <Number
            label="Cost"
            v-model="value.cost"
            :editMode="editMode"
        />
        <ManuscriptId
            offline
            label="원고 ID"
            v-model="value.manuscriptId"
            :editMode="editMode"
            @change="change"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'Publishing',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "publishings",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
