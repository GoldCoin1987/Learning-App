# Artificial Intelligence & Machine Learning — Lesson Plan

This is the modernization centerpiece of the curriculum. You bring a strong analytical foundation — a BS in Electronics Engineering, years as a radar technician, and a working career as a software developer — but AI/ML is the topic most likely to be genuinely *new* to you, so we build it from the ground up rather than assuming prior exposure. Nothing here is "too easy to cover": we start with what a model even *is* and end with you fine-tuning neural networks and deploying them to embedded targets. The payoff is dual. AI/ML is a standalone hireable skill (model building, data work, MLOps), and it is *also* the modern back-end of your old world — radar/RF signal classification, micro-Doppler target ID, RF fingerprinting, and spectrogram analysis are now done with the exact machine-learning tools in this module. You will keep meeting your radar past in new clothes.

**Prerequisites.** This module leans hard on Topic 01 (linear algebra — vectors, matrices, dot products, eigenvectors; probability — distributions, Bayes, expectation; calculus — derivatives, gradients, the chain rule). It integrates with Topic 08 (radar fundamentals, Doppler, the radar range equation) for the application thread and the capstone, and with Topic 04 (embedded systems) for the edge/TinyML deployment thread. Familiarity with Topic 02/03 DSP concepts (sampling, the FFT, spectrograms) makes the signal-feature work click. Do Topic 01 first; the rest can be cross-referenced as we go.

## Learning outcomes

By the end of this module you will be able to:

- Explain what AI, machine learning, and deep learning are, how they relate, and where each fits in the modern landscape (including the LLM era).
- Run the full ML workflow end to end: frame a problem, acquire and clean data, engineer features, split data correctly, train a model, evaluate it honestly, and deploy it.
- Implement and reason about the core classical ML algorithms (linear/logistic regression, decision trees and ensembles, SVM, k-NN, k-means, PCA) using NumPy and scikit-learn.
- Diagnose and treat overfitting/underfitting using the bias–variance lens, cross-validation, regularization, and the right metrics for the task.
- Engineer features from *signals* — FFT magnitude/phase, spectrograms, band energy, statistical moments — connecting DSP to ML.
- Build, train, and debug neural networks (perceptron → MLP → CNN → RNN/LSTM → transformer) in PyTorch, understanding backpropagation and gradient descent under the hood.
- Explain the modern LLM era: transformers, pretraining/fine-tuning, prompting, retrieval-augmented generation (RAG), and agents — and use a current frontier model (e.g., Claude Opus 4.8 / Claude Fable 5) via API.
- Apply ML to radar/RF: classify micro-Doppler signatures and spectrograms, understand RF fingerprinting, and build a CNN that classifies real or synthetic radar returns.
- Understand MLOps basics (experiment tracking, model versioning, monitoring, drift) and deploy a quantized model to an embedded/edge target (TinyML).

## Module breakdown

### Module 1: What AI/ML Is — The Landscape and the Mental Model

- **Concepts:**
  - Definitions and nesting: Artificial Intelligence ⊃ Machine Learning ⊃ Deep Learning. AI = systems that perform tasks we'd call "intelligent"; ML = systems that *learn patterns from data* instead of being explicitly programmed; DL = ML using many-layered neural networks.
  - The fundamental shift: traditional software is `rules + data → answers`; machine learning is `data + answers → rules (a model)`. Internalize this — it reframes everything you already do as a developer.
  - Types of learning: **supervised** (labeled data: classification, regression), **unsupervised** (no labels: clustering, dimensionality reduction), **self-supervised** (labels derived from the data itself — the engine behind modern LLMs), **reinforcement learning** (reward-driven sequential decisions). Brief mention of semi-supervised.
  - What a "model" actually is: a parameterized function `f(x; θ)` whose parameters `θ` are fit to data. Training = finding good `θ`. Inference = running `f` on new `x`.
  - The vocabulary you'll live in: features, labels, samples, training/validation/test sets, parameters vs. hyperparameters, loss/cost function, epoch, batch, generalization.
  - Where AI is in 2026: classical ML still runs most production tabular/industrial systems; deep learning dominates perception (vision, audio, signals); transformers/LLMs dominate language and increasingly multimodal and agentic systems.
  - Why now: data availability, GPU compute, and algorithmic advances (especially the transformer, 2017). A short, honest history — perceptron (1958), AI winters, the deep learning resurgence (2012, AlexNet), the transformer/LLM era.
- **Why it matters / connections:** This module gives you the map so nothing later feels like magic. For your domain: framing matters — "is there a target in this range bin?" is binary classification; "what kind of target?" is multi-class; "what's its velocity?" is regression; "group these unlabeled emitter signatures" is clustering. You already do rule-based DSP; ML is what you reach for when the rules are too complex or unknown to write by hand.
- **Hands-on / exercises:**
  - Set up your environment: install Python 3.11+, create a virtual environment, install `numpy`, `pandas`, `matplotlib`, `scikit-learn`, `jupyter`. Launch a Jupyter notebook. (As a developer this is trivial — but do it deliberately; it's your lab bench for the whole module.)
  - Written exercise: take five problems from your radar/RF experience and classify each as supervised/unsupervised, and within supervised as classification vs. regression. Write one sentence each on what `x` (features) and `y` (label) would be.
  - Load a built-in dataset (`sklearn.datasets.load_iris`), inspect its shape, print feature names and label distribution. Just *look* at data as arrays.
- **You've got it when:** You can draw the AI/ML/DL nesting diagram from memory, state the `data + answers → rules` reframing in your own words, and correctly categorize a new problem's learning type and identify its features and labels.

### Module 2: The ML Workflow — Data → Features → Train → Evaluate → Deploy

- **Concepts:**
  - The end-to-end pipeline as a repeatable discipline: problem framing → data acquisition → exploratory data analysis (EDA) → cleaning → feature engineering → train/test split → model selection → training → evaluation → tuning → deployment → monitoring. Most real ML effort is data, not modeling.
  - Data representation: the design matrix `X` (rows = samples, columns = features) and target vector `y`. Numerical vs. categorical vs. ordinal vs. text vs. signal data.
  - Data cleaning: missing values (drop, impute mean/median/model-based), outliers, duplicates, inconsistent units/scales, label errors.
  - Preprocessing: normalization vs. standardization (z-score), one-hot encoding for categoricals, handling class imbalance (resampling, class weights).
  - **The cardinal rule:** never let test data influence training. Fit scalers/encoders on the *training set only*, then apply to test. Data leakage is the #1 cause of "great in the lab, useless in the field" — a trap that maps directly to over-optimistic radar detection results.
  - The notion of a *baseline*: always establish a trivial baseline (predict the majority class, predict the mean) before celebrating a fancy model.
  - `scikit-learn`'s consistent API: `fit()`, `predict()`, `transform()`, `fit_transform()`, and the `Pipeline` object that chains preprocessing + model so leakage can't sneak in.
- **Why it matters / connections:** This is the skeleton every later module hangs on. The discipline of honest data splitting is what separates a hireable practitioner from someone who fools themselves. For radar: your "data acquisition" is pulse collection; your "features" might be FFT bins; your "leakage" risk is, e.g., having pulses from the same target dwell in both train and test sets — which inflates accuracy and collapses in deployment.
- **Hands-on / exercises:**
  - Take a tabular dataset (e.g., the Titanic dataset or `sklearn`'s breast cancer / wine datasets). Do EDA: histograms, correlations, missing-value counts, class balance.
  - Build a `Pipeline` with a `StandardScaler` + a simple classifier. Do a proper train/test split with `train_test_split`. Report accuracy against a majority-class baseline.
  - Deliberately *introduce* leakage (scale the full dataset before splitting), observe the optimistic bias, then fix it. Feel the difference.
- **You've got it when:** You can write a leak-free scikit-learn `Pipeline` from scratch, explain why scaler-fit-before-split is wrong, and you instinctively compute a baseline before trusting a model's score.

### Module 3: Classical ML I — Regression and the Geometry of Learning

- **Concepts:**
  - **Linear regression:** the model `ŷ = wᵀx + b`; the mean-squared-error loss; the closed-form normal equation `w = (XᵀX)⁻¹Xᵀy` (ties directly to Topic 01 linear algebra); and the iterative alternative, **gradient descent** (compute the gradient of the loss, step downhill, repeat). Learning rate intuition.
  - **Logistic regression:** for *classification*, not regression despite the name. The sigmoid function squashing a linear score into a probability; cross-entropy (log) loss; decision boundary as a hyperplane. The multi-class extension (softmax).
  - Gradient descent variants: batch, stochastic (SGD), mini-batch. Why mini-batch is the workhorse.
  - The cost-function-as-landscape mental model: training is descending a surface; convexity (linear/logistic loss is convex → one global minimum) vs. the non-convex landscapes of neural nets (coming later).
  - Feature scaling's effect on gradient descent (badly scaled features = elongated valleys = slow convergence) — connects back to Module 2 preprocessing.
- **Why it matters / connections:** Linear and logistic regression are the "Hello World" that contain, in miniature, *everything* deep learning does: a parameterized function, a loss, and gradient descent. Master these and neural nets are just stacked, nonlinear versions. Your math background (Topic 01) makes the normal equation and gradient derivation natural rather than mysterious. Logistic regression is a perfectly respectable radar CFAR-adjacent detector for simple feature sets.
- **Hands-on / exercises:**
  - **From scratch in NumPy:** implement linear regression two ways — the normal equation, and batch gradient descent. Plot the loss curve descending. Verify both give the same `w`.
  - Implement logistic regression from scratch in NumPy on a 2D toy dataset; plot the decision boundary.
  - Then do both in scikit-learn (`LinearRegression`, `LogisticRegression`) and confirm your hand-rolled versions match. This "scratch then library" pattern builds real understanding.
  - Vary the learning rate (too small, just right, too large/diverging) and watch the loss curve. Build intuition for this critical hyperparameter.
- **You've got it when:** You can derive the gradient of MSE by hand, implement gradient descent in ~15 lines of NumPy, explain why logistic regression is a classifier, and articulate what the learning rate does to convergence.

### Module 4: Classical ML II — Trees, SVMs, k-NN, and Ensembles

- **Concepts:**
  - **k-Nearest Neighbors (k-NN):** the simplest idea — classify by majority vote of the `k` closest training points. No training, all inference. Distance metrics (Euclidean, etc.); the curse of dimensionality; why scaling matters enormously here.
  - **Decision trees:** recursive splitting on features to reduce impurity (Gini, entropy). Highly interpretable; prone to overfitting. Stopping criteria and pruning.
  - **Ensembles:** the power of combining weak learners. **Bagging / Random Forests** (many trees on bootstrapped data + feature subsampling → variance reduction). **Boosting** (sequential error-correcting trees — AdaBoost, and the production workhorses **gradient boosting / XGBoost / LightGBM**). Why gradient-boosted trees still win most tabular ML competitions and industrial deployments in 2026.
  - **Support Vector Machines (SVM):** maximum-margin classification; support vectors; the **kernel trick** (implicitly mapping to high-dimensional spaces — RBF, polynomial kernels) to handle nonlinearly-separable data. Conceptually beautiful and historically dominant for signal classification before deep learning.
  - Model interpretability spectrum: trees (transparent) → linear models → ensembles → SVMs → neural nets (opaque). When interpretability is a requirement (regulated/safety-critical, like defense).
- **Why it matters / connections:** This is the practitioner's toolbox for *tabular and moderate-dimensional* data — which is much of real-world industrial ML, and a hireable skill in its own right. SVMs with RBF kernels were *the* standard for radar/sonar target classification and modulation recognition for two decades; understanding them tells you what deep learning replaced and why. Random forests and gradient boosting are excellent, fast baselines for engineered radar features (the FFT/statistical features from Module 6).
- **Hands-on / exercises:**
  - Use scikit-learn to fit k-NN, a decision tree, a random forest, gradient boosting, and an SVM (RBF kernel) on the same dataset. Compare accuracy and training time.
  - Visualize decision boundaries of each on a 2D dataset (e.g., `make_moons`, `make_circles`) — *see* how each algorithm carves up space differently. The SVM-RBF vs. linear-model contrast is especially instructive.
  - Plot feature importances from the random forest. Tune `k` in k-NN and watch the decision boundary go from jagged (low `k`, overfit) to smooth (high `k`).
  - Install and use XGBoost or LightGBM on a tabular dataset; compare to sklearn's gradient boosting.
- **You've got it when:** You can pick a sensible classical algorithm for a given problem and justify it, explain the kernel trick in plain language, articulate why ensembles beat single trees, and reach for gradient-boosted trees as a strong tabular baseline by reflex.

### Module 5: Model Evaluation — Honesty, Metrics, and the Bias–Variance Tradeoff

- **Concepts:**
  - **Train/validation/test split:** the three-way split and the role of each. The validation set is for tuning; the test set is touched *once*, at the end.
  - **Cross-validation:** k-fold CV for robust performance estimates on limited data; stratified CV for imbalanced classes; the leave-one-out extreme. Why CV beats a single split.
  - **Overfitting vs. underfitting:** memorizing the training data (high variance) vs. failing to capture the pattern (high bias). Learning curves (train vs. validation error as a function of training set size / model complexity) as the diagnostic tool.
  - **The bias–variance tradeoff:** total error decomposes into bias² + variance + irreducible noise. Model complexity trades one for the other; the goal is the sweet spot.
  - **Regularization** as the lever against overfitting: **L2 (Ridge)** shrinks weights, **L1 (Lasso)** drives weights to zero (feature selection), **elastic net** combines them. The regularization strength `λ` as a hyperparameter. (Preview: dropout and weight decay in neural nets are the same idea.)
  - **Metrics — and choosing the right one:** for classification, accuracy is *not enough* (deadly with imbalance). Confusion matrix; precision, recall, F1; the precision/recall tradeoff; ROC curve and AUC; precision-recall curves for imbalanced data. For regression: MSE, RMSE, MAE, R². The crucial skill of matching the metric to the real-world cost of errors.
  - Hyperparameter tuning: grid search, random search, and a nod to Bayesian optimization — always over the validation set / via CV, never the test set.
- **Why it matters / connections:** This module is what makes you *trustworthy*. Anyone can call `.fit()`; the value is in knowing whether the result is real. The radar tie is direct and visceral: a missed-target (false negative) and a false alarm (false positive) have wildly asymmetric costs, which is exactly the precision/recall/ROC framing — and is literally the same decision theory behind radar detection threshold setting (Topic 08). The ROC curve *originated* in radar. You already understand `Pd` (probability of detection) vs. `Pfa` (probability of false alarm); that *is* the ROC curve.
- **Hands-on / exercises:**
  - Take an overfitting-prone model (deep decision tree, high-degree polynomial regression) and plot learning curves and validation curves. Watch the train/validation gap signal overfitting.
  - Apply L1 and L2 regularization to a regression problem; plot coefficient magnitudes vs. `λ`; watch Lasso zero out features.
  - On an imbalanced classification problem, compute accuracy (misleadingly high), then the confusion matrix, precision, recall, F1, and plot the ROC and precision-recall curves. Set a decision threshold deliberately for a target recall.
  - Use `GridSearchCV` / `RandomizedSearchCV` with cross-validation to tune a model honestly.
- **You've got it when:** You can read a learning curve and diagnose bias vs. variance, choose and justify a metric for a cost-asymmetric problem, explain why accuracy lies on imbalanced data, and connect the ROC curve to the radar Pd/Pfa tradeoff you already know.

### Module 6: Feature Engineering — Especially on Signals (DSP Meets ML)

- **Concepts:**
  - General feature engineering: transformations (log, polynomial), interaction terms, binning, encoding, scaling (recap), and the art of injecting domain knowledge into features. The principle: good features can make a simple model excellent; bad features cripple any model.
  - **Feature engineering for signals — the bridge from your DSP world to ML:**
    - Time-domain features: statistical moments (mean, variance, skewness, kurtosis), zero-crossing rate, RMS power, peak/crest factor, autocorrelation features.
    - **Frequency-domain features via the FFT** (ties to Topics 02/03): spectral magnitude/phase, dominant frequency, spectral centroid/bandwidth/rolloff/flatness, band energy ratios, harmonic features.
    - **Time-frequency features — the spectrogram** (STFT): turning a 1D signal into a 2D image of how its frequency content evolves. This is *the* representation that lets you treat signal classification as image classification (sets up CNNs in Module 9).
    - For radar specifically: **micro-Doppler signatures** (the spectrogram of the Doppler shift over time — the modulation a target's moving parts impose, distinguishing a drone's rotors from a bird's wingbeats from a walking human's limbs), range-Doppler maps, and pulse-descriptor-word features.
  - Automatic feature learning preview: deep learning *learns* features from raw data, reducing (but not eliminating) the need for hand-engineering. Knowing both — hand-crafted and learned — is what makes you versatile.
  - Dimensionality and feature selection: filter methods (correlation, mutual information), wrapper methods (recursive feature elimination), embedded methods (Lasso, tree importances).
- **Why it matters / connections:** This is *your superpower module*. Your radar/RF/DSP background means you already understand signals deeply — and feature engineering on signals is precisely where that knowledge becomes ML gold. A radar engineer who can extract micro-Doppler features and feed them to a classifier is far more valuable than a generic ML practitioner who's never seen a spectrogram. This module is the explicit hinge between your past and your future.
- **Hands-on / exercises:**
  - Generate or obtain 1D signals (synthesize a few: sinusoids, chirps, noise, AM/FM-modulated tones — you can build these from your Topic 02/03 knowledge). Compute time-domain and FFT-based features in NumPy/SciPy.
  - Compute spectrograms with `scipy.signal.spectrogram` / `stft`; visualize them. Synthesize simple "micro-Doppler-like" signals (a carrier with a slowly varying sinusoidal Doppler modulation) and view their spectrograms.
  - Build a feature table from a set of signals and train a random forest / gradient-boosted classifier on the *engineered features* to classify signal type. This is a complete, hireable signal-classification pipeline using classical ML.
  - Use mutual information and tree importances to rank which signal features matter most.
- **You've got it when:** You can take a raw 1D signal and produce a meaningful feature vector (time + frequency + time-frequency), explain what a spectrogram represents and why it turns signal classification into image classification, and articulate what micro-Doppler features capture about a moving target.

### Module 7: Neural Networks From the Ground Up — Perceptron, MLPs, Backprop

- **Concepts:**
  - **The perceptron:** the original artificial neuron — weighted sum of inputs + bias, through an activation function. The biological analogy (and its limits). Why a single perceptron is just logistic regression and can't solve XOR.
  - **Multi-Layer Perceptrons (MLPs):** stacking neurons into layers (input → hidden → output) and stacking layers for depth. The universal approximation idea: enough hidden units can approximate any function.
  - **Activation functions:** why nonlinearity is essential (without it, stacked layers collapse to one linear layer). Sigmoid, tanh, and the modern default **ReLU** (and variants: leaky ReLU, GELU). Softmax for the output layer of a classifier.
  - **Forward propagation:** how an input flows through the network to a prediction — it's just chained matrix multiplications and activations (Topic 01 linear algebra everywhere).
  - **Backpropagation:** the algorithm that makes training possible — the chain rule (Topic 01 calculus) applied systematically to compute the gradient of the loss with respect to *every* weight, propagating error backward through the layers. Demystify it: it's automatic differentiation, bookkeeping the chain rule.
  - **Training loop:** forward pass → compute loss → backward pass (gradients) → update weights (gradient descent / SGD) → repeat. Epochs, batches, learning rate (recap from Module 3, now multi-layer).
  - Practical training concerns: weight initialization, vanishing/exploding gradients, batch normalization, dropout (regularization, recap Module 5 connection), and modern optimizers (SGD with momentum, **Adam**).
- **Why it matters / connections:** This is the conceptual heart of modern AI. Everything downstream — CNNs, RNNs, transformers, LLMs — is an MLP with structural twists. If you understand a forward pass, backprop, and the training loop, you understand the engine of the entire deep learning revolution. Your calculus and linear algebra (Topic 01) mean backprop is *learnable*, not hand-wavy, for you. Frame it explicitly: backprop is the chain rule at scale.
- **Hands-on / exercises:**
  - **Build a tiny MLP from scratch in NumPy** — forward pass, manual backprop for one hidden layer, training loop — and train it to solve XOR (the problem that killed the single perceptron). This is a rite of passage; do not skip it. Seeing backprop work in code you wrote yourself is transformative.
  - Plot the loss decreasing; visualize the learned decision boundary on XOR / `make_moons`.
  - Experiment: remove the nonlinearity and watch the network fail to learn XOR — proving why activations matter.
- **You've got it when:** You can explain forward and backward passes as matrix math + chain rule, you've trained a from-scratch NumPy MLP on XOR, and you can state why nonlinear activations are non-negotiable.

### Module 8: PyTorch and the Deep Learning Toolchain

- **Concepts:**
  - Why frameworks: hand-coding backprop doesn't scale. Frameworks provide **autograd** (automatic differentiation — exactly the backprop you just did by hand, automated), GPU acceleration, prebuilt layers, optimizers, and data loaders.
  - The landscape: **PyTorch** (the research and increasingly production standard, dynamic graphs, Pythonic) vs. TensorFlow/Keras (still widespread, especially in production/edge). We use PyTorch as the primary tool; you'll recognize Keras when you see it.
  - PyTorch fundamentals: **tensors** (NumPy arrays with autograd + GPU), `requires_grad` and the autograd engine, `nn.Module` for defining models, `nn.Linear`/`nn.ReLU`/etc. as building blocks, loss functions (`nn.CrossEntropyLoss`, `nn.MSELoss`), optimizers (`torch.optim.Adam`), and `DataLoader`/`Dataset` for batching.
  - The canonical PyTorch training loop: zero gradients → forward → loss → `loss.backward()` → `optimizer.step()`. Recognize this as the Module 7 loop, now with autograd doing the calculus.
  - GPU usage (`.to(device)`), and the reality of training: monitoring loss, saving/loading models (`state_dict`), reproducibility (seeds).
- **Why it matters / connections:** PyTorch is the tool you'll list on your résumé and use daily. Because you built backprop by hand in Module 7, autograd will feel like a labor-saving device rather than a black box — you'll *trust* it because you know what it's doing. This is the leverage point: from here you can build arbitrarily sophisticated models.
- **Hands-on / exercises:**
  - Install PyTorch. Re-implement your Module 7 MLP in PyTorch — far fewer lines, and now with autograd. Confirm it learns XOR / the same toy problem.
  - Train an MLP on a real dataset (MNIST digits is the classic; or your engineered signal features from Module 6). Get comfortable with the full loop, `DataLoader`, and evaluation.
  - Re-do the Module 6 signal classifier as a small neural net fed engineered features; compare to your random forest. Then (preview) feed it slightly rawer features and let the net learn more itself.
  - Save and reload a trained model.
- **You've got it when:** You can write a complete PyTorch training loop from memory, explain what autograd is doing (it's your hand-rolled backprop), and you've trained a real model on real data end to end.

### Module 9: Deep Learning Architectures — CNNs, RNNs/LSTMs, Transformers

- **Concepts:**
  - **Convolutional Neural Networks (CNNs):** the architecture for grid-like data (images — *and spectrograms*). Convolution as a learned, shift-invariant filter (this will resonate hard with your DSP/matched-filter background — a conv kernel *is* a learnable FIR filter). Feature maps, pooling (downsampling), hierarchical feature learning (edges → textures → objects). Why CNNs dominate vision and signal-as-image tasks. Classic architectures in brief (LeNet, AlexNet, ResNet and the residual-connection idea).
  - **Recurrent Neural Networks (RNNs):** the architecture for *sequences* (time series, signals as 1D sequences, text). Hidden state carrying memory across time steps. The vanishing-gradient problem over long sequences.
  - **LSTMs and GRUs:** gated RNN variants that solve long-range memory via gates (forget/input/output). The workhorses of sequence modeling before transformers.
  - **The transformer (2017, "Attention Is All You Need"):** the architecture that changed everything. **Self-attention** — every position attends to every other position, weighing relevance — replacing recurrence with parallelizable global context. Multi-head attention, positional encodings, the encoder/decoder structure. Why it scales and why it displaced RNNs for most sequence tasks (and increasingly vision, via Vision Transformers).
  - Choosing an architecture: CNN for spatial/spectrogram data, RNN/LSTM or 1D-CNN for sequences, transformer for long-range dependencies and large-scale pretraining. Transfer learning: using pretrained networks and fine-tuning — a critical practical skill that lets you succeed with limited data.
- **Why it matters / connections:** This is where signal/radar ML lives. **A spectrogram is an image, so a CNN classifies it** — that single sentence connects Module 6's spectrograms to the dominant modern technique for micro-Doppler classification, RF modulation recognition, and emitter identification. The convolution-as-learnable-filter insight maps your matched-filter intuition (Topic 08) directly onto deep learning. RNNs/1D-CNNs handle raw IQ sequences. The transformer is essential cultural and technical literacy for the LLM era (Module 11).
- **Hands-on / exercises:**
  - Build and train a small CNN in PyTorch on an image dataset (MNIST or CIFAR-10). Understand each layer's role.
  - **The key project:** take the spectrograms you generated in Module 6 (synthetic micro-Doppler-style signals of a few "classes") and train a CNN to classify them as images. This is the direct precursor to the capstone and a genuinely hireable radar-ML skill.
  - Build a small RNN/LSTM (or 1D-CNN) to classify or forecast a time-series / raw 1D signal.
  - Use a pretrained CNN (transfer learning, e.g., a ResNet) fine-tuned on a small image set to feel how transfer learning beats training from scratch on limited data.
  - Read/skim the transformer "Attention Is All You Need" architecture diagram and trace the data flow conceptually (full implementation is optional/advanced).
- **You've got it when:** You can explain why a CNN suits spectrograms (and connect convolution to FIR/matched filtering), distinguish when to use CNN vs. RNN vs. transformer, articulate what self-attention does, and you've trained a CNN that classifies spectrogram images.

### Module 10: Signal & Radar ML Applications — Your Domain, Modernized

- **Concepts:**
  - **Micro-Doppler classification:** the flagship radar-ML application. The micro-motions of a target (drone rotor blades, helicopter blades, human gait, animal motion) impose characteristic time-varying Doppler modulations; their spectrograms are distinctive "signatures" that CNNs classify with high accuracy. Drone-vs-bird detection, human activity recognition, vehicle classification. This is an active, fundable, hireable field (counter-UAS, defense, automotive radar).
  - **RF fingerprinting / specific emitter identification (SEI):** identifying *individual* transmitters by subtle hardware imperfections in their emissions (oscillator drift, amplifier nonlinearities, transient turn-on behavior) — a security/electronic-warfare application where ML classifies devices that are nominally identical. Features from IQ data, often fed to CNNs.
  - **Automatic Modulation Classification (AMC):** identifying the modulation scheme (BPSK, QAM, FM, etc.) of a received signal — historically SVMs on cumulant features, now CNNs/complex-valued nets on raw IQ or spectrograms. Core to cognitive radio and SIGINT.
  - **Spectrogram CNNs as the unifying pattern:** convert signal → spectrogram → treat as image → CNN. The single most transferable radar-ML workflow.
  - **Range-Doppler and SAR imagery with deep learning:** ML for target detection/classification in radar imagery; learned approaches to clutter suppression and CFAR.
  - Data realities in this domain: scarcity of labeled real data, heavy reliance on **synthetic data and simulation**, and **domain adaptation** (models trained on synthetic data degrading on real returns) — a serious, employable specialty.
  - The cognitive-radar / ML-in-the-loop frontier: adaptive waveform selection, learned detection.
- **Why it matters / connections:** This module is your *competitive differentiator* in the job market. The intersection of radar/RF expertise and modern ML is a high-demand, under-supplied niche — defense, aerospace, automotive radar, spectrum monitoring, counter-UAS. You bring the irreplaceable domain knowledge (what the signals *mean*, what micro-Doppler physically *is*, Topic 08); the earlier modules give you the ML. Few candidates have both. This is the explicit "AI as the back-end for radar/RF signal classification" thesis realized.
- **Hands-on / exercises:**
  - Survey project: read 2–3 accessible papers/articles on micro-Doppler CNN classification and RF fingerprinting; write a one-page summary of the standard pipeline each uses.
  - Extend the Module 9 spectrogram CNN: generate a richer set of synthetic micro-Doppler classes (e.g., "rotary-wing," "fixed-wing," "pedestrian," "clutter") with varying SNR, and build a robust classifier. Evaluate with the proper metrics from Module 5 (confusion matrix, per-class recall) — and explicitly relate false-alarm/missed-detection costs back to radar Pd/Pfa.
  - Optional/advanced: explore a public RF dataset (e.g., RadioML for modulation classification) and train a CNN on it.
  - Reflection exercise: map three tasks from your radar-technician days onto an ML formulation, identifying data source, features, model, and the failure modes you'd watch for.
- **You've got it when:** You can describe the spectrogram-CNN pipeline for micro-Doppler classification end to end, explain RF fingerprinting and AMC at a level you could discuss in an interview, and articulate the synthetic-data/domain-gap problem that makes this a specialist field.

### Module 11: The Modern LLM Era — Transformers at Scale, Fine-Tuning, Prompting, RAG, Agents

- **Concepts:**
  - **From transformer (Module 9) to Large Language Model:** scale the transformer up, train it on enormous text corpora via **self-supervised pretraining** (predict the next token), and emergent language capabilities appear. The pretraining → fine-tuning paradigm: a general "foundation model" adapted to specific tasks.
  - **Pretraining vs. fine-tuning vs. in-context learning:** pretraining (expensive, done by labs); fine-tuning (adapting a pretrained model to your data/task — full fine-tuning vs. parameter-efficient methods like LoRA); in-context learning / few-shot prompting (no weight updates — the model learns from examples in the prompt). Instruction tuning and RLHF (reinforcement learning from human feedback) for alignment.
  - **Prompting:** the practical interface to LLMs. Zero-shot, few-shot, system prompts, chain-of-thought reasoning. Prompt engineering as a real, if evolving, skill.
  - **Retrieval-Augmented Generation (RAG):** grounding an LLM in your own documents/data — embed documents into vectors, store in a vector database, retrieve relevant chunks at query time, and feed them to the model as context. The standard pattern for building knowledge-grounded assistants without retraining. Embeddings as a reusable concept (dense vector representations — connects to Module 12's PCA/representation ideas).
  - **Agents:** LLMs that *act* — using tools (function/tool calling), reasoning over multiple steps, and orchestrating workflows. The current frontier (agentic coding, research assistants, autonomous task completion). Tool use, planning, and the practical patterns (and pitfalls) of agentic systems.
  - **The current frontier (as of mid-2026):** the most capable models available are Anthropic's **Claude Opus 4.8** (`claude-opus-4-8`) and **Claude Fable 5** (`claude-fable-5`), Anthropic's most capable widely released model. Both offer very large (≈1M-token) context windows and strong reasoning/agentic performance. Multimodal models (text + image + audio) are now standard. The field moves fast — the *concepts* (transformer, pretrain/fine-tune, prompt, RAG, agent) are durable even as specific model names change.
  - Practical/operational realities: API-based usage vs. self-hosting open-weight models; cost and latency; hallucination and the need for grounding/verification; context windows; safety and evaluation.
- **Why it matters / connections:** This is the most *visible* part of the modernization play — "AI" in the public and hiring imagination largely *means* LLMs in 2026. As a software developer, LLM-powered application development (prompting, RAG, agents, tool-calling) is an immediately marketable skill. Crucially, you can now *understand* LLMs rather than treat them as magic, because you built up from perceptron → MLP → transformer. You can also use LLMs as a force multiplier — including as a coding and learning assistant throughout this very curriculum.
- **Hands-on / exercises:**
  - Use a frontier LLM via API (e.g., the Anthropic API with `claude-opus-4-8`): write a Python script that sends a prompt and processes the response. Experiment with system prompts, few-shot prompting, and chain-of-thought.
  - Build a minimal **RAG** system: take a small set of documents (e.g., your own notes, or radar/RF reference text), generate embeddings, store them, retrieve relevant chunks for a query, and have the LLM answer grounded in them. This is a complete, portfolio-worthy modern AI app.
  - Build a simple **tool-using agent**: give an LLM a "tool" (a Python function — e.g., a calculator, a signal-feature extractor, or a web lookup) and use the model's tool-calling to invoke it. Watch it plan and act.
  - Optional/advanced: fine-tune a small open-weight model (or use LoRA) on a small task-specific dataset to feel the fine-tuning workflow.
  - Reflection: write a short note on where an LLM is and isn't the right tool — e.g., why you'd still use a CNN, not an LLM, for micro-Doppler classification.
- **You've got it when:** You can explain the transformer → LLM → fine-tuning → prompting → RAG → agents progression in plain language, you've called a frontier model (Claude Opus 4.8 / Fable 5) from code, you've built a working RAG pipeline and a tool-using agent, and you can reason about when an LLM is or isn't the right tool.

### Module 12: Unsupervised Learning — Clustering and Dimensionality Reduction (PCA)

- **Concepts:**
  - **Clustering — finding structure without labels:** **k-means** (assign points to `k` centroids, iterate; choosing `k` via the elbow method / silhouette score); hierarchical clustering (dendrograms); **DBSCAN** (density-based, finds arbitrary shapes and outliers, no need to pre-specify cluster count). Applications: customer segmentation, anomaly detection, grouping unlabeled emitter signatures.
  - **Dimensionality reduction — Principal Component Analysis (PCA):** finding the directions of maximum variance (the eigenvectors of the covariance matrix — a direct, satisfying payoff to Topic 01 linear algebra). Projecting high-dimensional data onto a few principal components for compression, denoising, and visualization. Explained-variance ratio.
  - Modern nonlinear embedding/visualization: **t-SNE** and **UMAP** for visualizing high-dimensional data (e.g., clusters of learned features) in 2D. Autoencoders as a neural approach to dimensionality reduction (encoder → bottleneck → decoder) and anomaly detection.
  - **Anomaly / novelty detection:** unsupervised flagging of out-of-distribution samples — highly relevant to detecting *unknown* or *novel* radar threats that weren't in any training set.
- **Why it matters / connections:** Real-world signal data is often *unlabeled* — you have piles of intercepts with no ground truth. Unsupervised methods let you find structure, group similar emitters, reduce feature dimensionality before classification, and detect anomalies (novel/unknown signals — a critical EW/defense capability). PCA is also a beautiful, concrete application of the eigenvector machinery from Topic 01. Placed late deliberately: by now you appreciate why reducing dimensionality and visualizing learned features matters across everything you've built.
- **Hands-on / exercises:**
  - Run k-means on a 2D dataset; use the elbow method and silhouette score to pick `k`; compare with DBSCAN on non-spherical clusters.
  - Apply PCA to a high-dimensional dataset (e.g., your Module 6 signal feature vectors, or MNIST). Plot explained variance; reduce to 2D and visualize; reconstruct and observe compression loss.
  - Use t-SNE or UMAP to visualize the feature embeddings learned by your Module 9 CNN — *see* whether the classes cluster.
  - Build a simple anomaly detector: cluster "normal" signals, then flag a synthetic "novel" signal as an outlier.
- **You've got it when:** You can cluster unlabeled data and choose the number of clusters sensibly, explain PCA in terms of variance and eigenvectors (tying back to Topic 01), and articulate why unsupervised anomaly detection matters for spotting unknown signals.

### Module 13: MLOps Basics — From Notebook to Production

- **Concepts:**
  - Why MLOps: a model in a notebook is worthless; value comes from reliable, maintainable, monitored deployment. The gap between "it works on my machine" and "it serves predictions reliably."
  - **Experiment tracking and reproducibility:** logging hyperparameters, metrics, and artifacts (tools like MLflow, Weights & Biases); seeding; versioning *data* and *models*, not just code (DVC, model registries).
  - **Deployment patterns:** batch inference vs. real-time serving; wrapping a model behind an API (e.g., FastAPI); containerization (Docker — connects to your Topic 05 Linux skills); the model-as-a-service idea.
  - **Monitoring in production:** tracking latency/throughput, prediction quality, and — critically — **data drift and concept drift** (the world changes, the model goes stale). Triggering retraining.
  - The ML lifecycle as a loop, not a line: data → train → deploy → monitor → (drift detected) → retrain. CI/CD for ML (ties to Topic 10 engineering practice).
  - Practical hygiene: model serialization formats (ONNX as a portable, framework-agnostic format — important for the next module's edge deployment), inference optimization, and the cost/latency/accuracy tradeoffs of serving.
- **Why it matters / connections:** This is what makes you a *software engineer who does ML* rather than a notebook tinkerer — and it leans directly on skills you already have (Topic 05 Linux, Topic 06 networking, Topic 10 engineering practice, your existing developer experience with APIs and deployment). It's a major hireability multiplier: many people can train models; far fewer can ship and maintain them. The drift concept is especially relevant to radar — adversaries and environments change, and a deployed classifier must be monitored and updated.
- **Hands-on / exercises:**
  - Wrap one of your trained models (e.g., the signal classifier) behind a FastAPI endpoint that accepts input and returns a prediction. Test it with HTTP requests.
  - Containerize it with Docker; run it locally (leveraging Topic 05).
  - Add experiment tracking to one of your earlier training runs with MLflow or Weights & Biases; log metrics and compare runs.
  - Export a model to ONNX format (sets up Module 14).
  - Thought/design exercise: sketch a monitoring plan for a deployed micro-Doppler classifier — what would you log, and how would you detect that it's gone stale?
- **You've got it when:** You can serve a model behind an API, containerize it, track experiments reproducibly, and explain data/concept drift and why monitoring + retraining is essential.

### Module 14: Edge AI & TinyML — Running Models on Embedded Targets

- **Concepts:**
  - Why edge/on-device ML: latency (no round-trip to a server), privacy, bandwidth, offline operation, and power — all directly relevant to fielded radar/RF sensors and autonomous platforms. The contrast with cloud inference.
  - The constraints of embedded targets (recall Topic 04): limited RAM/flash, no FPU sometimes, tight power budgets, no OS or an RTOS. Models must be *small and cheap*.
  - **Model compression techniques:** **quantization** (float32 → int8, the workhorse — shrinks size ~4× and speeds up integer-only hardware; post-training quantization vs. quantization-aware training), **pruning** (removing unimportant weights), **knowledge distillation** (training a small "student" to mimic a large "teacher"), and efficient architectures (MobileNet, depthwise-separable convolutions).
  - **The TinyML toolchain:** training in PyTorch/TensorFlow → exporting (ONNX / TensorFlow Lite / TFLite Micro) → running on microcontrollers (Arm Cortex-M, ESP32) with frameworks like **TensorFlow Lite for Microcontrollers** or vendor tools (Edge Impulse, CMSIS-NN). Inference engines and hardware accelerators (NPUs, edge TPUs, DSP blocks).
  - The full embedded-ML pipeline: collect sensor data → train model on a workstation → compress/quantize → convert to embedded format → deploy and run inference on the MCU → act on results — all on a device the size of a coin.
  - The radar/RF tie: an embedded radar sensor that classifies micro-Doppler signatures *on the device* in real time, drawing milliwatts — the convergence of Topics 04, 08, and 09.
- **Why it matters / connections:** This module fuses your embedded background (Topic 04) with everything you've learned in AI, and it's a fast-growing, well-paid niche (smart sensors, wearables, industrial IoT, defense edge sensors). For someone with your radar-technician and embedded-engineering history, "TinyML on RF/radar sensors" is an almost bespoke career fit. It's the literal realization of "running models on the embedded targets from Topic 04."
- **Hands-on / exercises:**
  - Take a trained model (ideally your signal/spectrogram classifier) and apply **post-training quantization** (PyTorch or TFLite). Measure the size reduction and any accuracy change.
  - Convert a small model to TensorFlow Lite / TFLite Micro or ONNX; benchmark inference on your laptop CPU as a stand-in.
  - If you have embedded hardware from Topic 04 (e.g., an Arm Cortex-M board, ESP32, or a dev kit): deploy a tiny model and run inference on-device. Alternatively use Edge Impulse's workflow with a supported board.
  - Capstone-adjacent: deploy a small micro-Doppler/signal classifier to an embedded target (or emulate the constraints) and run it on streaming input.
  - Compare a full-precision vs. quantized model's size, speed, and accuracy in a table; reason about the tradeoff for a power-constrained sensor.
- **You've got it when:** You can quantize a model and explain the size/speed/accuracy tradeoff, describe the train-on-workstation → compress → convert → deploy-to-MCU pipeline, and you've run (or convincingly emulated) a small model on an embedded target — ideally a signal classifier.

## Capstone / integrative exercise

**Build an end-to-end radar/RF signal classifier with a neural network — from raw signal to deployed model — and document it as a portfolio project.**

This capstone deliberately threads together the whole module and explicitly links to Topic 08 (radar) and Topic 04 (embedded):

1. **Data (Modules 2, 6):** Generate (or obtain) a labeled dataset of several radar-relevant signal classes — e.g., synthetic micro-Doppler signatures for "rotary-wing drone," "fixed-wing," "pedestrian/walker," and "clutter/noise," across a range of SNRs. Use your DSP knowledge (Topics 02/03) to synthesize physically plausible signals: carriers with class-characteristic time-varying Doppler modulations. Split the data properly (no leakage — same-target dwells must not straddle train/test).
2. **Features / representation (Module 6, 9):** Convert each signal to a **spectrogram** (STFT). Treat the spectrogram as an image.
3. **Model (Modules 7–9):** Build and train a **CNN in PyTorch** to classify the spectrogram images. Establish a classical baseline first (engineered features + gradient-boosted trees or SVM, per Modules 4–6) so you can show the deep model earns its complexity.
4. **Evaluation (Module 5):** Evaluate honestly — confusion matrix, per-class precision/recall, ROC/AUC. **Explicitly relate false-alarm vs. missed-detection costs to radar Pd/Pfa** (Topic 08): the ROC curve you produce *is* the detection-theory tradeoff you knew as a radar tech, now learned from data.
5. **Inspect (Module 12):** Use t-SNE/UMAP on the CNN's learned features to visualize class separation; optionally add an anomaly detector to flag a "novel" class the model never trained on.
6. **Operationalize (Module 13):** Wrap the trained classifier behind a FastAPI endpoint and containerize it with Docker (Topic 05). Track your experiments.
7. **Edge deploy (Module 14):** **Quantize** the CNN and convert it to TFLite Micro / ONNX; deploy (or credibly emulate) it on an embedded target from Topic 04, running inference on streaming spectrograms. Report the size/speed/accuracy tradeoff.
8. **(Optional LLM tie-in, Module 11):** Build a small RAG/agent assistant over your project's documentation and radar references, or use a tool-calling agent that invokes your classifier as a tool.

**Deliverable:** a clean, documented repository (notebooks + scripts + README) showing the full pipeline, results, and tradeoffs — a single project that demonstrates classical ML, deep learning, proper evaluation, MLOps, and edge deployment, all on *your* domain. This is the artifact that proves the modernization and the radar-ML differentiator to a hiring manager.

## Common pitfalls & rust-knockers

- **Data leakage.** The most common and most dangerous mistake — fitting scalers/encoders on the full dataset before splitting, or letting correlated samples (e.g., consecutive pulses from one target) span train and test. Always split first; use `Pipeline`.
- **Trusting accuracy on imbalanced data.** 99% accuracy is worthless if 99% of samples are one class. Use the confusion matrix, precision/recall/F1, ROC/PR curves — and choose the metric by the real cost of each error type.
- **No baseline.** Always compare against a trivial baseline (majority class / mean / a simple model). A complex model that barely beats "predict the mean" isn't worth it.
- **Skipping the from-scratch builds.** It's tempting to jump straight to `sklearn`/PyTorch. Don't skip implementing gradient descent and backprop by hand (Modules 3, 7) — that's where understanding lives, and it's what makes the frameworks make sense.
- **Reaching for deep learning too early.** For tabular and moderate-dimensional problems, gradient-boosted trees often beat neural nets with far less data and fuss. Deep learning shines on perception (images, signals, audio, text), not everything.
- **Learning-rate and `max_tokens`-style hyperparameter neglect.** Too-high learning rate diverges; too-low crawls. Always plot the loss curve.
- **Treating LLMs as oracles.** They hallucinate. Ground them (RAG), verify outputs, and know when a purpose-built model (e.g., a CNN for micro-Doppler) is the right tool instead.
- **Underestimating the data work.** Most real ML time is data acquisition, cleaning, and feature engineering — not model architecture. Embrace it; for you, signal feature engineering is a strength, not a chore.
- **Forgetting the domain.** Your edge over a generic ML candidate is radar/RF knowledge. Don't bury it — lead with it. The physics (what micro-Doppler *is*, what Pd/Pfa *mean*) makes you better at the ML.
- **Reproducibility rust.** Set seeds, version data and models, track experiments. "It worked yesterday" is not a result.
- **Fear of the math.** You have a strong foundation (Topic 01). Backprop is the chain rule; PCA is eigenvectors; gradient descent is following a derivative downhill. Lean on what you know.

## Self-assessment checklist

- [ ] I can draw the AI ⊃ ML ⊃ DL nesting and categorize any problem's learning type, features, and labels.
- [ ] I can build a leak-free scikit-learn `Pipeline` and explain why test data must never touch training.
- [ ] I implemented linear and logistic regression *and* gradient descent from scratch in NumPy, and they match scikit-learn.
- [ ] I can choose and justify a classical algorithm (k-NN, tree, random forest, gradient boosting, SVM) for a given problem.
- [ ] I can read a learning curve to diagnose bias vs. variance, and apply regularization and cross-validation correctly.
- [ ] I choose evaluation metrics by error cost, and I can connect the ROC curve to radar Pd/Pfa.
- [ ] I can turn a raw 1D signal into a meaningful feature vector (time + FFT + spectrogram) and explain micro-Doppler.
- [ ] I implemented a from-scratch NumPy MLP with backprop and trained it on XOR, and I can explain forward/backward passes as matrix math + chain rule.
- [ ] I can write a complete PyTorch training loop from memory and explain what autograd is doing.
- [ ] I can explain when to use a CNN vs. RNN/LSTM vs. transformer, and why a spectrogram is classified by a CNN.
- [ ] I trained a CNN that classifies spectrogram images.
- [ ] I can describe micro-Doppler classification, RF fingerprinting, and AMC at interview level.
- [ ] I can explain the transformer → LLM → fine-tuning → prompting → RAG → agents progression, and I've called a frontier model (Claude Opus 4.8 / Fable 5) from code.
- [ ] I built a working RAG pipeline and a tool-using agent.
- [ ] I can cluster unlabeled data, choose `k` sensibly, and explain PCA via variance and eigenvectors.
- [ ] I can serve a model behind a FastAPI endpoint, containerize it with Docker, and explain data/concept drift.
- [ ] I can quantize a model, explain the size/speed/accuracy tradeoff, and deploy (or emulate) it on an embedded target.
- [ ] I completed the capstone: an end-to-end radar/RF signal classifier from raw signal to deployed, quantized model, with honest evaluation tied to Pd/Pfa.

## Canonical resources

**Foundational books**
- *Hands-On Machine Learning with Scikit-Learn, Keras & TensorFlow* — Aurélien Géron (3rd ed.). The single best practical, code-first ML book; covers Modules 2–9 thoroughly. Start here.
- *An Introduction to Statistical Learning* (ISL/ISLP) — James, Witten, Hastie, Tibshirani. The accessible theory companion (Python edition available, free PDF). Excellent for Modules 3–5, 12. (*The Elements of Statistical Learning* is the deeper, harder sibling for when you want rigor.)
- *Deep Learning* — Goodfellow, Bengio, Courville (free online). The definitive (math-heavy) deep learning reference for Modules 7–9; use as a reference, not a cover-to-cover read.
- *Pattern Recognition and Machine Learning* — Christopher Bishop. Classic, Bayesian-flavored; deep reference for classical ML.
- *Understanding Deep Learning* — Simon J.D. Prince (free PDF). Modern, exceptionally clear visual treatment of deep learning including transformers.

**Courses**
- Andrew Ng's *Machine Learning Specialization* (Coursera/DeepLearning.AI) — the canonical on-ramp; rebuilds intuition for Modules 1–7.
- Andrew Ng's *Deep Learning Specialization* (Coursera) — CNNs, RNNs, sequence models (Modules 7–9).
- *Practical Deep Learning for Coders* — fast.ai (free) — top-down, code-first; great for a developer who wants results fast.
- Hugging Face *NLP / LLM Course* (free) — transformers, fine-tuning, and the LLM ecosystem (Module 11).
- *TinyML* (HarvardX / Vijay Janapa Reddi) and **Edge Impulse** tutorials — for Module 14.
- 3Blue1Brown's *Neural Networks* video series (free, YouTube) — the best visual intuition for backprop and (now) transformers (Modules 7, 9).

**Tools & libraries**
- **Python**, **NumPy**, **pandas**, **Matplotlib**, **SciPy** (signal features), **Jupyter** — the lab.
- **scikit-learn** — classical ML (Modules 2–6, 12).
- **PyTorch** — deep learning primary framework (Modules 8–11); **TensorFlow/Keras** for recognition and TFLite edge work.
- **XGBoost / LightGBM** — gradient-boosted trees (Module 4).
- **Hugging Face Transformers / Datasets** — LLMs and pretrained models (Modules 9, 11).
- **Anthropic API** (`claude-opus-4-8`, `claude-fable-5`) and the Anthropic Python SDK — frontier LLM usage, RAG, agents (Module 11). LangChain/LlamaIndex for RAG/agent scaffolding (use judiciously).
- A **vector database** (FAISS, Chroma) for RAG (Module 11).
- **MLflow** or **Weights & Biases** — experiment tracking; **FastAPI** + **Docker** — serving (Module 13).
- **ONNX**, **TensorFlow Lite / TFLite Micro**, **CMSIS-NN**, **Edge Impulse** — edge/TinyML (Module 14).

**Domain-specific (radar/RF ML)**
- *Radar Micro-Doppler Signatures: Processing and Applications* — Chen, Tahmoush, Miceli (IET) — the reference on micro-Doppler (Module 10).
- **RadioML** datasets (DeepSig) — public RF/modulation-classification data for hands-on AMC work (Module 10).
- Survey papers: search for recent reviews on "deep learning for radar micro-Doppler classification," "RF fingerprinting / specific emitter identification with deep learning," and "automatic modulation classification with CNNs" — these map directly onto Module 10 and the capstone.
- IEEE journals (Trans. on Aerospace and Electronic Systems, Trans. on Geoscience and Remote Sensing) for current radar-ML research.
