package com.mayabot.nlp.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 搜索相关性评分算法
 *
 * @author hankcs
 */
class BM25 {
    /**
     * 文档句子的个数
     */
    int D;

    /**
     * 文档句子的平均长度
     */
    double avgdl;

    /**
     * 拆分为[句子[单词]]形式的文档
     */
    List<List<String>> docs;

    /**
     * 文档中每个句子中的每个词与词频
     */
    ArrayList<Map<String, Integer>> f;

    /**
     * 文档中全部词语与出现在几个句子中
     */
    Map<String, Integer> df;

    /**
     * IDF
     */
    Map<String, Double> idf;

    /**
     * 调节因子
     */
    final static float k1 = 1.5f;

    /**
     * 调节因子
     */
    final static float b = 0.75f;

    public BM25(List<List<String>> docs) {
        this.docs = docs;
        D = docs.size();
        for (List<String> sentence : docs) {
            avgdl += sentence.size();
        }
        avgdl /= D;
        f = new ArrayList<>(D);
        for (int i = 0; i < D; i++) {
            f.add(null);
        }
        
        df = new TreeMap<>();
        idf = new TreeMap<>();
        init();
    }

    /**
     * 在构造时初始化自己的所有参数
     */
    private void init() {
        int index = 0;
        for (List<String> sentence : docs) {
            Map<String, Integer> tf = new TreeMap<String, Integer>();
            for (String word : sentence) {
                Integer freq = tf.get(word);
                freq = (freq == null ? 0 : freq) + 1;
                tf.put(word, freq);
            }
            f.set(index,tf);
            for (Map.Entry<String, Integer> entry : tf.entrySet()) {
                String word = entry.getKey();
                Integer freq = df.get(word);
                freq = (freq == null ? 0 : freq) + 1;
                df.put(word, freq);
            }
            ++index;
        }
        for (Map.Entry<String, Integer> entry : df.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();
            idf.put(word, Math.log(D - freq + 0.5) - Math.log(freq + 0.5));
        }
    }

    public double sim(List<String> sentence, int index) {
        double score = 0;
        for (String word : sentence) {
            if (!f.get(index).containsKey(word)) {
                continue;
            }
            int d = docs.get(index).size();
            Integer wf = f.get(index).get(word);
            score += (idf.get(word) * wf * (k1 + 1)
                    / (wf + k1 * (1 - b + b * d
                    / avgdl)));
        }

        return score;
    }

    public double[] simAll(List<String> sentence) {
        double[] scores = new double[D];
        for (int i = 0; i < D; ++i) {
            scores[i] = sim(sentence, i);
        }
        return scores;
    }
}
